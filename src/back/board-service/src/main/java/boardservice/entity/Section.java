package boardservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "section")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una sección almacenada en la plataforma.")
@Builder
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(
            description = "Identificador único de la imagen.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            nullable = false
    )
    private UUID id;

    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nombre de la imagen.",
            example = "casa.png",
            nullable = false
    )
    private String name;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    @Schema(
            description = "Imagen asociada al pictograma.",
            nullable = false
    )
    private Image image;

    @ManyToOne
    @JoinColumn(name = "language_code", nullable = false)
    @Schema(
            description = "Idioma asociado a la imagen.",
            nullable = false
    )
    private Language language;

    @Column(name = "owner_id", nullable = false)
    @Schema(
            description = "Identificador del propietario de la imagen.",
            example = "660e8400-e29b-41d4-a716-446655440000",
            nullable = false
    )
    private UUID ownerId;

    @Column(name = "is_public", nullable = false)
    @Schema(
            description = "Indica si la imagen es pública o privada.",
            example = "false",
            nullable = false
    )
    private boolean isPublic;

    @OneToMany(mappedBy = "sectionId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Pictogramas en el grid 5x6 con sus posiciones.")
    @Builder.Default
    private List<SectionPictogram> pictogramPositions = new ArrayList<>();
}
