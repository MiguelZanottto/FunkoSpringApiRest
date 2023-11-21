package org.develop.funkos.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
public class FunkoUpdateDto {
    @Length(min = 3, message = "El nombre debe contener al menos 3 letras")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    private String imagen;
    @Pattern(regexp = "(?i)^(SERIE|DISNEY|SUPERHEROES|PELICULAS|OTROS)$", message = "La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS")
    private String categoria;
    private Boolean isActivo;
    
    @JsonCreator
    public FunkoUpdateDto(
            @JsonProperty("nombre") String nombre,
            @JsonProperty("precio") double precio,
            @JsonProperty("cantidad") int cantidad,
            @JsonProperty("imagen") String imagen,
            @JsonProperty("categoria") String categoria,
            @JsonProperty("isActivo") Boolean isActivo
    ) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.categoria = categoria;
        this.isActivo = isActivo;
    }
}


