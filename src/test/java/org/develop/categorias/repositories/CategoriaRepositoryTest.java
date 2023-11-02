package org.develop.categorias.repositories;

import org.develop.categorias.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoriaRepositoryTest {
    @Autowired
    private CategoriasRepository categoriasRepository;
    @Autowired
    private TestEntityManager entityManager;
    Categoria categoria = new Categoria(null, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);

    @BeforeEach
    void setup() {
        entityManager.merge(categoria);
        entityManager.flush();
    }

    @Test
    void findAll(){
        // Act
        List<Categoria> listaCategories = categoriasRepository.findAll();

        // Assert
        assertAll(
                () -> assertNotNull(listaCategories),
                () -> assertFalse(listaCategories.isEmpty())
        );
    }

    @Test
    void findAllByNombre(){
        // Act
        String nombreExpected = "OTROS";
        List<Categoria> listaCategories = categoriasRepository.findAllByNombreContainingIgnoreCase("otros");

        // Assert
        assertAll(
                () -> assertNotNull(listaCategories),
                () -> assertFalse(listaCategories.isEmpty()),
                () -> assertEquals(nombreExpected, listaCategories.get(0).getNombre())
        );
    }

    @Test
    void findByID() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);

        // Assert
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertEquals("OTROS", categoria.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Categoria categoria = categoriasRepository.findById(100L).orElse(null);

        // Assert
        assertNull(categoria);
    }


    @Test
    void save() {
        // Act
        Categoria nuevaCategoria = new Categoria(null, "SUPERHEROES", LocalDateTime.now(), LocalDateTime.now(), true);
        Categoria savedCategoria = categoriasRepository.save(nuevaCategoria);

        // Assert
        assertAll("save",
                () -> assertNotNull(savedCategoria),
                () -> assertEquals("SUPERHEROES", savedCategoria.getNombre())
        );
    }


    @Test
    void update() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);
        Categoria categoriaActualizada = categoriasRepository.save(new Categoria(categoria.getId(), "SERIE", LocalDateTime.now(), LocalDateTime.now(), true));

        // Assert
        assertAll("update",
                () -> assertNotNull(categoriaActualizada),
                () -> assertEquals("SERIE", categoriaActualizada.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);
        categoriasRepository.deleteById(categoria.getId());
        Categoria categoriaBorrada = categoriasRepository.findById(1L).orElse(null);

        // Assert
        assertNull(categoriaBorrada);
    }

}

