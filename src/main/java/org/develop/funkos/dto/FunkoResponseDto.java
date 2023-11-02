package org.develop.funkos.dto;


import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FunkoResponseDto {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private String imagen;
    private String categoria;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
