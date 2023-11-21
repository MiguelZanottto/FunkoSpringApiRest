package org.develop.funkos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class FunkoCreateDto {
    @Length(min = 3, message = "El nombre debe contener al menos 3 letras")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    private String imagen;
    @Pattern(regexp = "(?i)^(SERIE|DISNEY|SUPERHEROES|PELICULAS|OTROS)$", message = "La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS")
    @NotBlank(message = "La categoria no puede estar vacía")
    private String categoria;
}
