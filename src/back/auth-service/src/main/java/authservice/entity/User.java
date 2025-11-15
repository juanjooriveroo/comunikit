package authservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "Representa la entidad de Usuario.")
public class User {
    @Id
    @Schema(
            description = "Identificador único del usuario.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            format = "UUID",
            type = "UUID",
            nullable = false
    )
    private UUID id;

    @Column(nullable = false, unique = true)
    @Schema(
            description = "Nombre de usuario.",
            example = "Francisco",
            format = "String",
            type = "String",
            nullable = false
    )
    private String name;

    @Column(nullable = false, unique = true)
    @Schema(
            description = "Correo electrónico del usuario. Será validado posteriormente por correo",
            example = "jugador@example.com",
            format = "String",
            type = "String",
            nullable = false
    )
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    @Schema(
            description = "Contraseña del usuario (no se muestra NUNCA).",
            format = "String",
            type = "String",
            nullable = false
    )
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @Schema(
            description = "Rol asignado al usuario. Relación ManyToOne con la tabla de roles.",
            example = "{ \"id\": 1, \"name\": \"ADMIN\" }",
            nullable = false,
            implementation = Rol.class
    )
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "language_code")
    @Schema(
            description = "Idioma preferido del usuario. Relación ManyToOne con la tabla de idiomas.",
            example = "{ \"code\": \"es\", \"name\": \"Español\" }",
            nullable = false,
            implementation = Language.class
    )
    private Language language;

    @Column(nullable = false)
    @Schema(
            description = "Tamaño en megabytes de almacenamiento de imágenes de pictográmas",
            example = "23.43",
            type = "Float",
            nullable = false
    )
    private Float storage_used;

    @Column(nullable = false)
    @Schema(
            description = "Estado de la cuenta (si ha sido activada o no)",
            example = "true",
            type = "Boolean",
            nullable = false
    )
    private Boolean activated;
}