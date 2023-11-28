package org.develop.rest.categorias.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDto {
    @Pattern(regexp = "(?i)^(SERIE|DISNEY|SUPERHEROES|PELICULAS|OTROS)$", message = "La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS")
    private String nombre;
    private Boolean isActivo;
}
