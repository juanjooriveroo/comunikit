package authservice.service;

import authservice.dto.*;
import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.exception.*;
import authservice.kafka.KafkaEventPublisher;
import authservice.mapper.UserMapper;
import authservice.entity.User;
import authservice.repository.UserRepository;
import authservice.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UUID testUserId;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testEmail = "test@example.com";
        testPassword = "password123";

        testUser = User.builder()
                .id(testUserId)
                .email(testEmail)
                .name("Test User")
                .password("encodedPassword")
                .activated(true)
                .build();
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValidAndAccountIsActivated() {
        LoginRequestDto loginRequest = new LoginRequestDto(testEmail, testPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, testUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");

        TokenResponseDto result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, testUser.getPassword());
        verify(jwtUtils).generateToken(testUser);
    }

    @Test
    void login_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        LoginRequestDto loginRequest = new LoginRequestDto("nonexistent@example.com", testPassword);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(passwordEncoder, jwtUtils);
    }

    @Test
    void login_ShouldThrowPasswordNotCorrectException_WhenPasswordIsInvalid() {
        LoginRequestDto loginRequest = new LoginRequestDto(testEmail, "wrongPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        assertThrows(PasswordNotCorrectException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void login_ShouldThrowAccountNotActivatedException_WhenAccountIsNotActivated() {
        testUser.setActivated(false);
        LoginRequestDto loginRequest = new LoginRequestDto(testEmail, testPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, testUser.getPassword())).thenReturn(true);

        AccountNotActivatedException exception = assertThrows(
                AccountNotActivatedException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("La cuenta no ha sido activada. Recibirás un mail para la activación", exception.getMessage());
        verify(eventPublisher).publishUserRegistered(any(UserRegisteredEvent.class));
    }

    @Test
    void register_ShouldSaveUserAndPublishEvent() {
        RegisterRequestDto registerRequest = new RegisterRequestDto("New User", testEmail, testPassword, "USER", "es");
        when(userMapper.toEntityFromRegisterRequest(registerRequest)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        RegisterResponseDto result = authService.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.request());
        verify(userMapper).toEntityFromRegisterRequest(registerRequest);
        verify(eventPublisher).publishUserRegistered(any(UserRegisteredEvent.class));
        verify(userRepository).save(testUser);
    }

    @Test
    void activate_ShouldActivateUserAndReturnToken() {
        testUser.setActivated(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateToken(testUser)).thenReturn("activation-token");
        when(userRepository.save(testUser)).thenReturn(testUser);

        TokenResponseDto result = authService.activate(testUserId);

        assertNotNull(result);
        assertEquals("activation-token", result.token());
        assertTrue(testUser.getActivated());
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        verify(jwtUtils).generateToken(testUser);
    }

    @Test
    void activate_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.activate(testUserId));
        verify(userRepository).findById(testUserId);
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void confirmNewPassword_ShouldUpdatePassword_WhenNewPasswordIsDifferent() {
        String newPassword = "newPassword123";
        String currentEncodedPassword = "encodedPassword";
        String newEncodedPassword = "encodedNewPassword";

        ConfirmNewPasswordRequestDto request = new ConfirmNewPasswordRequestDto(testUserId, newPassword);

        testUser.setPassword(currentEncodedPassword);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(newPassword, currentEncodedPassword)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

        authService.confirmNewPassword(request);

        verify(userRepository).findById(testUserId);
        verify(passwordEncoder).matches(newPassword, currentEncodedPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);

        assertEquals(newEncodedPassword, testUser.getPassword());
    }

    @Test
    void confirmNewPassword_ShouldThrowPasswordDuplicateException_WhenNewPasswordIsSame() {
        String samePassword = "samePassword";
        ConfirmNewPasswordRequestDto request = new ConfirmNewPasswordRequestDto(testUserId, samePassword);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(samePassword, testUser.getPassword())).thenReturn(true);

        assertThrows(PasswordDuplicateException.class, () -> authService.confirmNewPassword(request));
        verify(userRepository).findById(testUserId);
        verify(passwordEncoder).matches(samePassword, testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void recoveryAccount_ShouldPublishRecoveryEvent_WhenUserExists() {
        RecoveryAccountRequestDto request = new RecoveryAccountRequestDto(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        authService.recoveryAccount(request);

        verify(userRepository).findByEmail(testEmail);
        verify(eventPublisher).publishRecoveryAccount(any(UserRecoveryAccountEvent.class));
    }

    @Test
    void recoveryAccount_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        RecoveryAccountRequestDto request = new RecoveryAccountRequestDto("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.recoveryAccount(request));
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(eventPublisher);
    }
}