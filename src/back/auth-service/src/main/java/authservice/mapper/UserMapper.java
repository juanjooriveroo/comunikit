package authservice.mapper;

import authservice.dto.RegisterRequestDto;
import authservice.entity.Language;
import authservice.entity.Rol;
import authservice.entity.User;
import authservice.repository.LanguageRepository;
import authservice.repository.RolRepository;
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
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .rol(rol)
                .activated(false)
                .language(language)
                .storage_used(0F)
                .build();
    }
}
