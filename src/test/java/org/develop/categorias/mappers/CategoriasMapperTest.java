package org.develop.categorias.mappers;

import org.develop.rest.categorias.dto.CategoriaDto;
import org.develop.rest.categorias.mappers.CategoriasMapper;
import org.develop.rest.categorias.models.Categoria;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoriasMapperTest {

    private final CategoriasMapper mapper = new CategoriasMapper();

    private final CategoriaDto categoriaDto = new CategoriaDto("SUPERHEROES", false);
    private final Categoria categoria = new Categoria(1L, "PELICULAS", LocalDateTime.now(), LocalDateTime.now(), true);

    @Test
    void testToCategoria_create() {
        // Act
        Categoria categoriaNueva = mapper.toCategoria(categoriaDto);

        // Assert
        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), categoriaNueva.getNombre()),
                () -> assertTrue(categoriaNueva.getIsActivo()),
                () -> assertNotNull(categoriaNueva.getFechaCreacion()),
                () -> assertNotNull(categoriaNueva.getFechaActualizacion()),
                () -> assertNull(categoriaNueva.getId())
        );
    }

    @Test
    void testToCategoria_update(){
        // Act
        Categoria categoriaActualizada = mapper.toCategoria(categoriaDto, categoria);

        // Assert
        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), categoriaActualizada.getNombre()),
                () -> assertEquals(categoriaDto.getIsActivo(), categoriaActualizada.getIsActivo()),
                () -> assertEquals(categoria.getId(), categoriaActualizada.getId()),
                () -> assertEquals(categoria.getFechaCreacion(), categoriaActualizada.getFechaCreacion())
        );
    }
}
