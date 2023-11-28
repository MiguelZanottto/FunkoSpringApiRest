package org.develop.funkos.repositories;

import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.funkos.models.Funko;
import org.develop.rest.funkos.repositories.FunkosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode = never")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FunkoRepositoryTest {
    private final Categoria categoria = new Categoria(null, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(null, "TEST-1", 19.99, 100, "test1.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(null, "TEST-2", 14.99, 59, "test2.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

    @Autowired
    private FunkosRepository funkosRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        // Categoria
        entityManager.merge(categoria);
        entityManager.flush();
        // Funkos
        entityManager.merge(funko1);
        entityManager.merge(funko2);
        entityManager.flush();
    }

    @Test
    void findAll(){
        // Act
        List<Funko> funkoList = funkosRepository.findAll();

        // Assert
        assertAll(
                () -> assertNotNull(funkoList),
                () -> assertTrue(funkoList.size() >= 2),
                () -> assertFalse(funkoList.isEmpty())
        );
    }

    @Test
    void findById_ExistId(){
        // Act
        Long id = 1L;
        Optional<Funko> funko = funkosRepository.findById(id);

        // Assert
        assertAll(
                () -> assertNotNull(funko),
                () -> assertTrue(funko.isPresent()),
                () -> assertEquals(id, funko.get().getId())
        );
    }

    @Test
    void findById_NotExistId(){
        // Act
        Long id = 99L;
        Optional<Funko> funko = funkosRepository.findById(id);

        // Assert
        assertAll(
                () -> assertNotNull(funko),
                () -> assertTrue(funko.isEmpty())
        );
    }

    @Test
    void existById_true(){
        // Act
        Long id = 1L;

        Boolean existe = funkosRepository.existsById(id);

        // Assert
        assertAll(
                () -> assertTrue(existe)
        );
    }

    @Test
    void existById_false(){
        // Act
        Long id = 99L;

        Boolean existe = funkosRepository.existsById(id);

        // Assert
        assertAll(
                () -> assertFalse(existe)
        );
    }

    @Test
    void save(){
        Funko nuevoFunko =  new Funko(null, "NUEVO-FUNKO", 17.99, 33, "nuevofunko.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

        // Act
        Funko savedFunko = funkosRepository.save(nuevoFunko);
        List<Funko> listaFunko = funkosRepository.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedFunko),
                () -> assertTrue(funkosRepository.existsById(savedFunko.getId())),
                () -> assertTrue(listaFunko.size() >= 2)
        );
    }

    @Test
    void save_alreadyExist(){
        Funko nuevoFunko =  new Funko(1L, "NUEVO-FUNKO", 17.99, 33, "nuevofunko.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

        // Act
        Funko savedFunko = funkosRepository.save(nuevoFunko);
        List<Funko> listaFunko = funkosRepository.findAll();

        // Assert
        assertAll("save",
                () -> assertEquals(nuevoFunko, savedFunko),
                () -> assertNotNull(savedFunko),
                () -> assertTrue(funkosRepository.existsById(savedFunko.getId())),
                () -> assertTrue(listaFunko.size() >= 2)
        );
    }

    @Test
    void deleteById(){
        // Act
        Long id = 1L;
        funkosRepository.deleteById(1L);
        List<Funko> listaFunkos = funkosRepository.findAll();

        assertAll("delete",
                () -> assertFalse(funkosRepository.existsById(id)),
                () -> assertFalse(listaFunkos.isEmpty())
        );
    }
}
