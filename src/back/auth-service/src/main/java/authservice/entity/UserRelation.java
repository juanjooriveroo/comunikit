package authservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_relation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representa la entidad de relacion entre usuarios.")
public class UserRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(
            description = "Identificador único del usuario.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            format = "UUID",
            type = "UUID",
            nullable = false
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    @Schema(
            description = "Usuario del tutor.",
            format = "User",
            type = "User",
            nullable = false
    )
    private User tutor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(
            description = "Usuario dependiente.",
            format = "User",
            type = "User",
            nullable = false
    )
    private User user;
}