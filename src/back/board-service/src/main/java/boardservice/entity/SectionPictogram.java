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
@Table(name = "section_pictogram")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Relación entre sección y pictograma con posición en el grid 5x6.")
@IdClass(SectionPictogramId.class)
public class SectionPictogram {

    @Id
    @Column(name = "section_id")
    private UUID sectionId;

    @Id
    @Column(name = "col")
    @Schema(description = "Columna en el grid (0-4)", example = "2")
    private Integer col;

    @Id
    @Column(name = "row")
    @Schema(description = "Fila en el grid (0-5)", example = "3")
    private Integer row;

    @ManyToOne
    @JoinColumn(name = "pictogram_id", nullable = false)
    @Schema(description = "Pictograma en esta posición")
    private Pictogram pictogram;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class SectionPictogramId implements Serializable {
    private UUID sectionId;
    private Integer col;
    private Integer row;
}
