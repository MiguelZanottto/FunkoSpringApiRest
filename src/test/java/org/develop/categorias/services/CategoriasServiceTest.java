package org.develop.categorias.services;

import org.develop.rest.categorias.dto.CategoriaDto;
import org.develop.rest.categorias.exceptions.CategoriaNotFound;
import org.develop.rest.categorias.mappers.CategoriasMapper;
import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.categorias.repositories.CategoriasRepository;
import org.develop.rest.categorias.services.CategoriasServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriasServiceTest {
    private Categoria categoria1 = new Categoria(1L, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    private Categoria categoria2 = new Categoria(2L, "SUPERHEROES", LocalDateTime.now(), LocalDateTime.now(), true);


    @Mock
    private CategoriasRepository categoriasRepository;
    @Mock
    private CategoriasMapper categoriasMapper;
    @InjectMocks
    private CategoriasServiceImpl categoriasService;


    @Test
    void findAll(){
        // Arrange
        List<Categoria> categoriaList = List.of(categoria1, categoria2);
        String categoriaNombre = null;

        when(categoriasRepository.findAll()).thenReturn(categoriaList);

        // Act
        List <Categoria> res = categoriasService.findAll(categoriaNombre);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(2, res.size())
        );

        verify(categoriasRepository, times(1)).findAll();
    }

    @Test
    void findAllByName(){
        // Arrange
        List<Categoria> categoriaList = List.of(categoria1);
        String categoriaNombre = "OTROS";

        when(categoriasRepository.findAllByNombreContainingIgnoreCase(categoriaNombre)).thenReturn(categoriaList);

        // Act
        List <Categoria> res = categoriasService.findAll(categoriaNombre);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(categoriaNombre, res.get(0).getNombre())
        );
        verify(categoriasRepository, times(1)).findAllByNombreContainingIgnoreCase(categoriaNombre);
    }


    @Test
    void findById(){
        // Arrange
        Long id = 1L;

        when(categoriasRepository.findById(1L)).thenReturn(Optional.of(categoria1));

        // Act
        Categoria res = categoriasService.findById(1L);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals(categoria1, res)
        );
        verify(categoriasRepository, times(1)).findById(1L);
    }

    @Test
    void findById_notExistId(){
        // Arrange
        Long id = 99L;

        when(categoriasRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriasService.findById(id));

        assertEquals("Categoría con id 99 no encontrada", res.getMessage());

        verify(categoriasRepository, times(1)).findById(id);
    }


    @Test
    void findByName(){
        // Arrange
        String nombreCategoria = "OTROS";

        when(categoriasRepository.findByNombreEqualsIgnoreCase(nombreCategoria)).thenReturn(Optional.of(categoria1));

        // Act
        Categoria res = categoriasService.findByNombre(nombreCategoria);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(nombreCategoria, res.getNombre()),
                () -> assertEquals(categoria1, res)
        );
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(nombreCategoria);
    }

    @Test
    void findByNombre_notExistNombre(){
        // Arrange
        String nombreCategoria = "PEPE_EL_GRILLO";

        when(categoriasRepository.findByNombreEqualsIgnoreCase(nombreCategoria)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriasService.findByNombre(nombreCategoria));

        assertEquals("Categoría "+nombreCategoria+" no encontrada", res.getMessage());

        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(nombreCategoria);
    }

    @Test
    void save(){
        // Arrange
        Categoria nuevaCategoria = new Categoria(3L, "SUPERHEROES", LocalDateTime.now(), LocalDateTime.now(), true);
        CategoriaDto categoriaDto = new CategoriaDto("SUPERHEROES", true);

        when(categoriasRepository.save(nuevaCategoria)).thenReturn(nuevaCategoria);
        when(categoriasMapper.toCategoria(categoriaDto)).thenReturn(nuevaCategoria);

        // Act
        Categoria res = categoriasService.save(categoriaDto);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(nuevaCategoria, res)
        );

        verify(categoriasRepository, times(1)).save(nuevaCategoria);
        verify(categoriasMapper, times(1)).toCategoria(categoriaDto);
    }

    @Test
    void update(){
        // Arrange
        Categoria categoriaActual = categoria2;
        CategoriaDto categoriaDto = new CategoriaDto("SUPERHEROES", true);

        when(categoriasRepository.findById(any(Long.class))).thenReturn(Optional.of(categoriaActual));
        when(categoriasMapper.toCategoria(categoriaDto, categoriaActual)).thenReturn(categoriaActual);
        when(categoriasRepository.save(categoriaActual)).thenReturn(categoriaActual);

        // Act
        Categoria categoriaActualizada = categoriasService.update(categoriaActual.getId(), categoriaDto);

        // Assert
        assertAll(
                () -> assertNotNull(categoriaActualizada),
                () -> assertEquals(categoriaActual, categoriaActualizada)
        );
    }

    @Test
    void update_notExistId(){
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("SUPERHEROES", true);

        when(categoriasRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriasService.update(2L, categoriaDto));

        assertEquals("Categoría con id 2 no encontrada", res.getMessage());

        verify(categoriasRepository, times(1)).findById(2L);
    }

    @Test
    void deleteById(){
        // Arrange
        Long id = 1L;

        when(categoriasRepository.findById(id)).thenReturn(Optional.of(categoria1));

        // Act
        categoriasService.deleteById(id);

        verify(categoriasRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_idNotExist(){
        // Arrange
        Long id = 99L;

        when(categoriasRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () ->  categoriasService.deleteById(id));

        assertEquals("Categoría con id 99 no encontrada", res.getMessage());

        verify(categoriasRepository, times(1)).findById(id);
        verify(categoriasRepository, times(0)).deleteById(id);
    }

}
