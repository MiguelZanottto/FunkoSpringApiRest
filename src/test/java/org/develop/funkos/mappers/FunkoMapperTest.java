package org.develop.funkos.mappers;

import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.funkos.dto.FunkoCreateDto;
import org.develop.rest.funkos.dto.FunkoResponseDto;
import org.develop.rest.funkos.dto.FunkoUpdateDto;
import org.develop.rest.funkos.mappers.FunkoMapper;
import org.develop.rest.funkos.models.Funko;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {
    private final FunkoMapper funkosMapper = new FunkoMapper();
    Categoria categoria1 = new Categoria(1L, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    Categoria categoria2 = new Categoria(2L, "PELICULAS", LocalDateTime.now(), LocalDateTime.now(), true);


    @Test
    void testToFunko_create(){
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("FunkoTest1")
                .precio(19.99)
                .cantidad(10)
                .imagen("funkotest.jpg")
                .categoria("OTROS")
                .build();
        // Act
        Funko funkoNuevo = funkosMapper.toFunko(funkoCreateDto, categoria1);

        // Assert
        assertAll(
                () -> assertNull(funkoNuevo.getId()),
                () -> assertEquals(funkoCreateDto.getNombre(), funkoNuevo.getNombre()),
                () -> assertEquals(funkoCreateDto.getPrecio(), funkoNuevo.getPrecio()),
                () -> assertEquals(funkoCreateDto.getCantidad(), funkoNuevo.getCantidad()),
                () -> assertEquals(funkoCreateDto.getImagen(), funkoNuevo.getImagen()),
                () -> assertEquals(funkoCreateDto.getCategoria(), funkoNuevo.getCategoria().getNombre()),
                () -> assertNotNull(funkoNuevo.getFechaActualizacion()),
                () -> assertNotNull(funkoNuevo.getFechaCreacion()),
                () -> assertTrue(funkoNuevo.getIsActivo())
        );
    }

    @Test
    void testToFunko_update(){
        // Arrange
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest2")
                .precio(15.99)
                .cantidad(100)
                .imagen("funkotest2.jpg")
                .categoria("PELICULAS")
                .build();
        Funko funko = new Funko(1L, "FunkoTest3", 23.88, 20, "funkotest3.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria1);

        // Act
        Funko funkoActualizado = funkosMapper.toFunko(funkoUpdateDto, funko , categoria2);

        // Assert
        assertAll(
                () -> assertEquals(funkoUpdateDto.getNombre(), funkoActualizado.getNombre()),
                () -> assertEquals(funkoUpdateDto.getPrecio(), funkoActualizado.getPrecio()),
                () -> assertEquals(funkoUpdateDto.getCantidad(), funkoActualizado.getCantidad()),
                () -> assertEquals(funkoUpdateDto.getImagen(), funkoActualizado.getImagen()),
                () -> assertEquals(funkoUpdateDto.getCategoria(), funkoActualizado.getCategoria().getNombre()),
                () -> assertEquals(funko.getId(), funkoActualizado.getId()),
                () -> assertEquals(funko.getFechaCreacion(), funkoActualizado.getFechaCreacion()),
                () -> assertNotNull(funkoActualizado.getFechaActualizacion())
        );
    }

    @Test
    void toResponseDtoTest() {
        // Arrange
        Funko funko = new Funko(1L, "FunkoTest3", 23.88, 20, "funkotest3.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria1);

        // Act
        FunkoResponseDto funkoResponseDto = funkosMapper.toFunkoResponseDto(funko);

        // Assert
        assertAll(
                () -> assertEquals(funko.getNombre(), funkoResponseDto.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoResponseDto.getPrecio()),
                () -> assertEquals(funko.getCantidad(), funkoResponseDto.getCantidad()),
                () -> assertEquals(funko.getImagen(), funkoResponseDto.getImagen()),
                () -> assertEquals(funko.getCategoria().getNombre(), funkoResponseDto.getCategoria()),
                () -> assertEquals(funko.getId(), funkoResponseDto.getId()),
                () -> assertEquals(funko.getFechaCreacion(), funkoResponseDto.getFechaCreacion()),
                () -> assertEquals(funko.getFechaActualizacion(), funkoResponseDto.getFechaActualizacion())
        );
    }
}
