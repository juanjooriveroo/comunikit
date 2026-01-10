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
@Table(name = "board")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un tablero de comunicación.")
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(
            description = "Identificador único del tablero.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            nullable = false
    )
    private UUID id;

    @Column(name = "owner_id", nullable = false, unique = true)
    @Schema(
            description = "Identificador del propietario del tablero.",
            example = "660e8400-e29b-41d4-a716-446655440000",
            nullable = false
    )
    private UUID ownerId;

    @ManyToOne
    @JoinColumn(name = "language_code", nullable = false)
    @Schema(
            description = "Idioma del tablero.",
            nullable = false
    )
    private Language language;

    @Column(name = "is_public", nullable = false)
    @Schema(
            description = "Indica si el tablero es público o privado.",
            example = "false",
            nullable = false
    )
    private boolean isPublic;

    @OneToMany(mappedBy = "boardId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Secciones en el grid 5x6 con sus posiciones.")
    @Builder.Default
    private List<BoardSection> sectionPositions = new ArrayList<>();

    @OneToMany(mappedBy = "boardId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Pictogramas en el grid 5x6 con sus posiciones.")
    @Builder.Default
    private List<BoardPictogram> pictogramPositions = new ArrayList<>();
}
