package org.develop.rest.categorias.mappers;

import org.develop.rest.categorias.dto.CategoriaDto;
import org.develop.rest.categorias.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriasMapper {

    public Categoria toCategoria(CategoriaDto dto) {
        return new Categoria(
                null,
                dto.getNombre().toUpperCase(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
    }

    public Categoria toCategoria(CategoriaDto dto, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                dto.getNombre() != null ? dto.getNombre() : categoria.getNombre(),
                categoria.getFechaCreacion(),
                LocalDateTime.now(),
                dto.getIsActivo() != null ? dto.getIsActivo() : categoria.getIsActivo()
        );
    }
}