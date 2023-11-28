package org.develop.rest.funkos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.develop.rest.funkos.models.Funko;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@Schema(description = "Funko a crear")
public class FunkoCreateDto {

    @Length(min = 3, message = "El nombre debe contener al menos 3 letras")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del funko", example = "Cristiano Ronaldo")
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio del funko", example = "100.0")
    private Double precio;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Schema(description = "Cantidad del funko", example = "20")
    private Integer cantidad;

    @Schema(description = "Imagen del funko", example = Funko.IMAGE_DEFAULT)
    private String imagen;

    @Pattern(regexp = "(?i)^(SERIE|DISNEY|SUPERHEROES|PELICULAS|OTROS)$", message = "La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS")
    @NotBlank(message = "La categoria no puede estar vacía")
    @Schema(description = "Categoria del funko", example = "SUPERHEROES")
    private String categoria;
}
