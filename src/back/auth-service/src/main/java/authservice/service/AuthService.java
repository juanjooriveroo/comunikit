package authservice.service;

import authservice.dto.*;
import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.exception.AccountNotActivatedException;
import authservice.exception.PasswordDuplicateException;
import authservice.exception.PasswordNotCorrectException;
import authservice.exception.UserNotFoundException;
import authservice.kafka.KafkaEventPublisher;
import authservice.mapper.UserMapper;
import authservice.entity.User;
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
}