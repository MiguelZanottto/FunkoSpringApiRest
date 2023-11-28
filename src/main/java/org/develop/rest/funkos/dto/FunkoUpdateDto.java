package org.develop.rest.funkos.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.develop.rest.funkos.models.Funko;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@Schema(description = "Funko a actualizar")
public class FunkoUpdateDto {
    @Length(min = 3, message = "El nombre debe contener al menos 3 letras")
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
    @Schema(description = "Categoria del funko", example = "SUPERHEROES")
    private String categoria;
    @Schema(description = "Si el funko esta activo", example = "true")
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


