package boardservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "image")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una imagen almacenada en la plataforma.")
@Builder
public class Image {

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

    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    @Schema(
            description = "Contenido binario de la imagen.",
            nullable = false
    )
    private byte[] image;

    @Column(name = "mime_type", nullable = false, length = 50)
    @Schema(
            description = "MIME type original de la imagen.",
            example = "image/png",
            nullable = false
    )
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    @Schema(
            description = "Tamaño de la imagen en bytes tras la compresión.",
            example = "15324",
            nullable = false
    )
    private long sizeBytes;

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

    @Column(name = "public", nullable = false)
    @Schema(
            description = "Indica si la imagen es pública o privada.",
            example = "false",
            nullable = false
    )
    private boolean isPublic;

}