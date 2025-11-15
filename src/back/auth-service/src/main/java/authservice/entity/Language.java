package authservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "language")
@Data
@Schema(description = "Entidad que representa un idioma disponible en la plataforma.")
public class Language {

    @Id
    @Schema(
            description = "Código ISO del idioma.",
            example = "es",
            type = "String",
            nullable = false
    )
    private String code;

    @Column(nullable = false)
    @Schema(
            description = "Nombre descriptivo del idioma.",
            example = "Español",
            type = "String",
            nullable = false
    )
    private String name;
}
