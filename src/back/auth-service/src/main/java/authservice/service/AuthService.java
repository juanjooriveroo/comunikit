package authservice.service;

import authservice.dto.*;
import authservice.entity.Language;
import authservice.entity.UserRelation;
import authservice.event.UserDeleteRequestEvent;
import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.exception.*;
import authservice.kafka.KafkaEventPublisher;
import authservice.mapper.UserMapper;
import authservice.entity.User;
import authservice.repository.LanguageRepository;
import authservice.repository.UserRelationRepository;
import authservice.repository.UserRepository;
import authservice.utils.JwtUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final LanguageRepository languageRepository;

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

        if (user.getActivated().equals(false)) {
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
     * Registra nuevo usuario y publica evento
     */
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        User user = userMapper.toEntityFromRegisterRequest(request);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId(String.valueOf(UUID.randomUUID()))
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getName())
                .timestamp(LocalDateTime.now())
                .build();

        eventPublisher.publishUserRegistered(event);

        userRepository.save(user);

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

            if (!userToEdit.getTutors().getId().equals(currentUser.getId())) {
                throw new UserNotTutorException("Usuario no válido por su rol");
            }
        } else {
            userToEdit = currentUser;
        }

        if (!passwordEncoder.matches(request.password(), userToEdit.getPassword())) {
            throw new PasswordNotCorrectException("La contraseña introducida no es correcta");
        }

        // Eliminar todas las relaciones donde este usuario es dependiente
        userRelationRepository.deleteByUser(userToEdit);
        
        // Eliminar todas las relaciones donde este usuario es tutor
        userRelationRepository.deleteByTutor(userToEdit);
        
        userRepository.delete(userToEdit);
    }

    /**
     * Crea un usuario dependiente a la cuenta que genera la petición, quedando vinculado a la cuenta tutora
     */
    @Transactional
    public CreateUserResponseDto createUser(CreateUserRequestDto request, String userId) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        return switch (currentUser.getRol().getName()) {
            case "ADMIN" -> {
                User userTutor = userRepository.findById(request.userId())
                        .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

                if (!userTutor.getRol().getName().equals("TUTOR")) {
                    throw new UserNotTutorException("Usuario no válido por su rol");
                }

                yield createUserDependent(request, userTutor);
            }
            case "TUTOR" -> createUserDependent(request, currentUser);
            default -> throw new UserNotTutorException("Usuario no válido por su rol");
        };
    }

    /**
     * Metodo para editar el perfil que se le pase por el request o el perfil propio si no viene ninguno.
     */
    @Transactional
    public void editProfile(String userId, EditProfileRequestDto request) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        User userToEdit;

        if (request.userId() != null){
            userToEdit = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        } else {
            userToEdit = currentUser;
        }

        switch (currentUser.getRol().getName()) {
            case "ADMIN" -> {
                editAndSave(userToEdit, request);
            }
            case "TUTOR" -> {
                if (!(userToEdit.getId().equals(currentUser.getId()) ||
                        userToEdit.getTutors().getId().equals(currentUser.getId()))) {
                    throw new UserNotTutorException("Usuario no válido por su rol");
                }
                editAndSave(userToEdit, request);
            }
            default -> throw new UserNotTutorException("Usuario no válido por su rol");
        }
    }

    /**
     * Metodo privado que nos permite, en caso de que haya datos, editar y guardar nuestro
     * usuario en la base de datos.
     */
    private void editAndSave(User userToEdit, EditProfileRequestDto request) {
        if (request.email() != null) userToEdit.setEmail(request.email());
        if (request.name() != null) userToEdit.setName(request.name());
        if (request.language() != null) {
            Language language = languageRepository.findById(request.language())
                    .orElseThrow(() -> new RuntimeException("Idioma no encontrado"));
            userToEdit.setLanguage(language);
        }
        userRepository.save(userToEdit);
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

            if (!userToEdit.getTutors().getId().equals(currentUser.getId())) {
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

    /**
     * Metodo privado que se dedica a generar un usuario nuevo y almacenarlo en base de datos.
     * Retorna el nuevo username y el uuid
     */
    private CreateUserResponseDto createUserDependent(CreateUserRequestDto request, User currentUser) {
        User newUser = userMapper.toEntityFromCreateUserRequest(request);
        User savedUser = userRepository.save(newUser);

        UserRelation relation = UserRelation.builder()
                .user(savedUser)
                .tutor(currentUser)
                .build();
        userRelationRepository.save(relation);

        return CreateUserResponseDto.builder()
                .username(savedUser.getUsername())
                .idUser(savedUser.getId())
                .build();
    }

    public GetAllDependentsAccountsResponseDto getAllDependentsAccounts(String userID) {
        User currentUser = userRepository.findById(UUID.fromString(userID))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        GetAllDependentsAccountsResponseDto response = new GetAllDependentsAccountsResponseDto(new ArrayList<>());
        currentUser.getDependents().forEach(dependentAccount -> {
            response.accounts().add(userMapper.toDtoDependentList(dependentAccount));
        });

        return response;
    }


    public DependentAccountDto getDependentAccount(String userID, String id) {
        User requestedUser = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        if (!requestedUser.getTutors().getId().equals(UUID.fromString(userID))) {
            throw new UserNotTutorException("Usuario no válido por su rol");
        }

        return userMapper.toDto(requestedUser);
    }
}