package org.develop.funkos.mappers;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.develop.categorias.models.Categoria;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoResponseDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.models.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class FunkoMapper {
    public Funko toFunko(FunkoCreateDto dto, Categoria categoria) {
        return new Funko(
                null,
                dto.getNombre(),
                dto.getPrecio(),
                dto.getCantidad(),
                dto.getImagen(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                categoria
        );
    }

    public Funko toFunko(FunkoUpdateDto dto, Funko funko, Categoria categoria) {
        return new Funko(
                 funko.getId(),
                 dto.getNombre() != null ? dto.getNombre() : funko.getNombre(),
                 dto.getPrecio() != null ? dto.getPrecio()   : funko.getPrecio(),
                 dto.getCantidad() != null ? dto.getCantidad() : funko.getCantidad(),
                 dto.getImagen() != null ? dto.getImagen() : funko.getImagen(),
                 funko.getFechaCreacion(),
                 LocalDateTime.now(),
                 dto.getIsActivo() != null ? dto.getIsActivo() : funko.getIsActivo(),
                 categoria);
    }

    public FunkoResponseDto toFunkoResponseDto(Funko funko) {
        return new FunkoResponseDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getImagen(),
                funko.getCategoria().getNombre(),
                funko.getFechaCreacion(),
                funko.getFechaActualizacion());
    }
}
