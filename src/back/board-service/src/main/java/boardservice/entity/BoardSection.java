package boardservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "board_section")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Relación entre tablero y sección con posición en el grid 5x6.")
@IdClass(BoardSectionId.class)
public class BoardSection {

    @Id
    @Column(name = "board_id")
    private UUID boardId;

    @Id
    @Column(name = "col")
    @Schema(description = "Columna en el grid (0-4)", example = "2")
    private Integer col;

    @Id
    @Column(name = "row")
    @Schema(description = "Fila en el grid (0-5)", example = "3")
    private Integer row;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @Schema(description = "Sección en esta posición")
    private Section section;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class BoardSectionId implements Serializable {
    private UUID boardId;
    private Integer col;
    private Integer row;
}
