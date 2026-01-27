package authservice.service;

import authservice.dto.*;
import authservice.event.UserDeleteRequestEvent;
import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.exception.*;
import authservice.kafka.KafkaEventPublisher;
import authservice.mapper.UserMapper;
import authservice.entity.User;
import authservice.repository.UserRelationRepository;
import authservice.repository.UserRepository;
import authservice.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para gestión de autenticación de usuarios
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserMapper userMapper;
    private final KafkaEventPublisher eventPublisher;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    /**
     * Autentica usuario y genera token JWT
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new PasswordNotCorrectException("La contraseña introducida no es correcta");
        }

        if (!user.getActivated()) {
            UserRegisteredEvent event = UserRegisteredEvent.builder()
                    .eventId(String.valueOf(UUID.randomUUID()))
                    .userId(user.getId().toString())
                    .email(user.getEmail())
                    .username(user.getName())
                    .timestamp(LocalDateTime.now())
                    .build();

            eventPublisher.publishUserRegistered(event);

            throw new AccountNotActivatedException("La cuenta no ha sido activada. Recibirás un mail para la activación");
        }

        return TokenResponseDto.builder()
                .token(jwtUtils.generateToken(user))
                .build();
    }

    /**
     * Autentica usuario por username y comprueba que su rol sea de tipo 'user' antes de emitir token.
     */
    @Transactional
    public TokenResponseDto loginByUsername(LoginByUsernameRequestDto request) {
        var users = userRepository.findByUsername(request.username());
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        // Buscar el usuario activado cuyo rol sea de tipo 'USUARIO' o 'USER'
        User matched = null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(request.username())) {
                matched = u;
                break;
            }
        }

        if (matched == null) {
            matched = users.get(0);
        }

        if (!passwordEncoder.matches(request.password(), matched.getPassword())) {
            throw new PasswordNotCorrectException("La contraseña introducida no es correcta");
        }

        if (!matched.getActivated()) {
            throw new AccountNotActivatedException("La cuenta no ha sido activada. Recibirás un mail para la activación");
        }

        String roleName = matched.getRol() != null ? matched.getRol().getName() : null;
        if (roleName == null || (!roleName.equalsIgnoreCase("USUARIO") && !roleName.equalsIgnoreCase("USER"))) {
            throw new PasswordNotCorrectException("Usuario no tiene rol de usuario permitido");
        }

        return TokenResponseDto.builder()
                .token(jwtUtils.generateToken(matched))
                .build();
    }

    /**
     * Registra nuevo usuario y publica evento
     */
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        User user = userMapper.toEntityFromRegisterRequest(request);

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId(String.valueOf(UUID.randomUUID()))
                .userId(savedUser.getId().toString())
                .email(savedUser.getEmail())
                .username(savedUser.getName())
                .timestamp(LocalDateTime.now())
                .build();

        eventPublisher.publishUserRegistered(event);

        return RegisterResponseDto.builder()
                .request(true)
                .build();
    }

    /**
     * Activa cuenta de usuario y genera token
     */
    @Transactional
    public TokenResponseDto activate(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        user.setActivated(true);

        userRepository.save(user);

        return TokenResponseDto.builder()
                .token(jwtUtils.generateToken(user))
                .build();
    }

    /**
     * Confirma y actualiza contraseña del usuario
     */
    @Transactional
    public void confirmNewPassword(ConfirmNewPasswordRequestDto request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new PasswordDuplicateException("La contraseña no puede ser la misma");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Inicia proceso de recuperación de cuenta
     */
    @Transactional
    public void recoveryAccount(RecoveryAccountRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        UserRecoveryAccountEvent event = UserRecoveryAccountEvent.builder()
                .eventId(String.valueOf(UUID.randomUUID()))
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getName())
                .build();

        eventPublisher.publishRecoveryAccount(event);
    }

    /**
     * Publica evento para enviar el correo de baja de cuenta
     */
    @Transactional
    public void deleteRequest(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        UserDeleteRequestEvent event = UserDeleteRequestEvent.builder()
                .eventId(String.valueOf(UUID.randomUUID()))
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getName())
                .build();

        eventPublisher.publishDeleteRequest(event);
    }

    /**
     * Elimina la cuenta si la contraseña coincide
     */
    @Transactional
    public void deleteAccount(DeleteAccountRequestDto request, String userId) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        User userToEdit;

        if (request.userId() != null){
            userToEdit = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            if (userToEdit.getTutors() == null || !userToEdit.getTutors().getId().equals(currentUser.getId())) {
                throw new UserNotTutorException("Usuario no válido por su rol");
            }
        } else {
            userToEdit = currentUser;
        }

        User passwordOwner = (userToEdit.getId().equals(currentUser.getId())) ? userToEdit : currentUser;

        if (!passwordEncoder.matches(request.password(), passwordOwner.getPassword())) {
            throw new PasswordNotCorrectException("La contraseña introducida no es correcta");
        }

        userRelationRepository.deleteByUser(userToEdit);
        
        userRelationRepository.deleteByTutor(userToEdit);
        
        userRepository.delete(userToEdit);
    }

    /**
     * Cambia la contraseña del usuario verificando que la anterior sea correcta
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordRequestDto request) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        User userToEdit;

        if (request.userId() != null){
            userToEdit = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            if (userToEdit.getTutors() == null || !userToEdit.getTutors().getId().equals(currentUser.getId())) {
                throw new UserNotTutorException("Usuario no válido por su rol");
            }
        } else {
            userToEdit = currentUser;
        }

        if (!passwordEncoder.matches(request.oldPassword(), userToEdit.getPassword())) {
            throw new PasswordNotCorrectException("La contraseña anterior no es correcta");
        }

        if (passwordEncoder.matches(request.newPassword(), userToEdit.getPassword())) {
            throw new PasswordDuplicateException("La contraseña no puede ser la misma");
        }

        userToEdit.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(userToEdit);
    }
}