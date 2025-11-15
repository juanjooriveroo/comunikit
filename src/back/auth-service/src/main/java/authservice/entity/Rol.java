package authservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Data
@Schema(description = "Entidad que representa un rol del sistema.")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @Schema(
            description = "Identificador único del rol.",
            example = "1",
            type = "Integer",
            nullable = false
    )
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(
            description = "Nombre del rol disponible en el sistema.",
            example = "ADMIN",
            type = "String",
            nullable = false
    )
    private String name;
}