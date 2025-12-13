package boardservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "pictogram", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "language_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un pictograma.")
@Builder
public class Pictogram {

    @Id
    @Schema(
            description = "Identificador único del pictograma.",
            example = "550e8400-e29b-41d4-a716-446655440002",
            nullable = false
    )
    private UUID id;

    @Column(nullable = false, length = 50)
    @Schema(
            description = "Nombre del pictograma (único por idioma).",
            example = "casa",
            nullable = false
    )
    private String name;

    @ManyToOne
    @JoinColumn(name = "language_code", nullable = false)
    @Schema(
            description = "Idioma del pictograma.",
            nullable = false
    )
    private Language language;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    @Schema(
            description = "Imagen asociada al pictograma.",
            nullable = false
    )
    private Image image;

    @Column(name = "is_public", nullable = false)
    @Schema(
            description = "Indica si la imagen es pública o privada.",
            example = "false",
            nullable = false
    )
    private boolean isPublic;

    @Column(name = "owner_id", nullable = false)
    @Schema(
            description = "Identificador del usuario propietario.",
            example = "550e8400-e29b-41d4-a716-446655440003",
            nullable = false
    )
    private UUID ownerId;
}