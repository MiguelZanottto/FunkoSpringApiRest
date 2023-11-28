package org.develop.rest.funkos.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.develop.rest.funkos.models.Funko;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Funko a devolver")
public class FunkoResponseDto {
    @Schema(description = "Identificador del funko", example = "1")
    private Long id;
    @Schema(description = "Nombre del funko", example = "Cristiano Ronaldo")
    private String nombre;
    @Schema(description = "Precio del funko", example = "100.0")
    private Double precio;
    @Schema(description = "Cantidad del funko", example = "20")
    private Integer cantidad;
    @Schema(description = "Imagen del funko", example = Funko.IMAGE_DEFAULT)
    private String imagen;
    @Schema(description = "Categoria del funko", example = "SUPERHEROES")
    private String categoria;
    @Schema(description = "Fecha de creacion del funko", example = "2021-01-01T00:00:00.000Z")
    private LocalDateTime fechaCreacion;
    @Schema(description = "Fecha de actualizacion del funko", example = "2021-01-01T00:00:00.000Z")
    private LocalDateTime fechaActualizacion;
}
