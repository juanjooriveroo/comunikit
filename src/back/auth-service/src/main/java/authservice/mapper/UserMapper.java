package authservice.mapper;

import authservice.dto.CreateUserRequestDto;
import authservice.dto.DependentAccountDto;
import authservice.dto.RegisterRequestDto;
import authservice.entity.Language;
import authservice.entity.Rol;
import authservice.entity.User;
import authservice.repository.LanguageRepository;
import authservice.repository.RolRepository;
import authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapeador entre DTOs y entidades de usuario
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    private final RolRepository rolRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Convierte DTO de registro a entidad User
     */
    public User toEntityFromRegisterRequest(RegisterRequestDto request) {
        Rol rol = rolRepository.findByName(request.rol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Language language = languageRepository.findById(request.language())
                .orElseThrow(() -> new RuntimeException("Idioma no encontrado"));

        return User.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .username(generateUsername(request.name()))
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .rol(rol)
                .activated(false)
                .language(language)
                .storage_used(0F)
                .build();
    }

    public User toEntityFromCreateUserRequest(CreateUserRequestDto request) {
        Rol rol = rolRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Language language = languageRepository.findById(request.language())
                .orElseThrow(() -> new RuntimeException("Idioma no encontrado"));

        return User.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .username(generateUsername(request.name()))
                .password(passwordEncoder.encode(request.password()))
                .email(null)
                .rol(rol)
                .activated(true)
                .language(language)
                .storage_used(0F)
                .build();
    }

    private String generateUsername(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        String firstInitial = parts[0].substring(0, 1).toLowerCase();
        String firstSurname = parts.length > 1 ? parts[1].toLowerCase() : "";
        String username = firstInitial + firstSurname;
        long counts = userRepository.countUserByUsernameStartingWith(username);
        return username + String.format("%02d", counts + 1);
    }

    public DependentAccountDto toDto(User dependentAccount) {
        return DependentAccountDto.builder()
                .id(dependentAccount.getId())
                .name(dependentAccount.getName())
                .username(dependentAccount.getUsername())
                .language(dependentAccount.getLanguage())
                .storage_used(dependentAccount.getStorage_used())
                .build();
    }

    public DependentAccountDto toDtoDependentList(User dependentAccount) {
        return DependentAccountDto.builder()
                .id(dependentAccount.getId())
                .name(dependentAccount.getName())
                .username(dependentAccount.getUsername())
                .language(null)
                .storage_used(null)
                .build();
    }
}
