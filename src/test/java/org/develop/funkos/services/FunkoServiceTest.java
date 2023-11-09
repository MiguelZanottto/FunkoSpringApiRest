package org.develop.funkos.services;

import org.develop.categorias.exceptions.CategoriaNotFound;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.services.CategoriasService;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.exceptions.FunkoNotFound;
import org.develop.funkos.mappers.FunkoMapper;
import org.develop.funkos.models.Funko;
import org.develop.funkos.repositories.FunkosRepository;
import org.develop.notifications.config.WebSocketConfig;
import org.develop.notifications.config.WebSocketHandler;
import org.develop.notifications.mappers.FunkoNotificationMapper;
import org.develop.notifications.models.Notificacion;
import org.develop.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceTest {
    private final Categoria categoria = new Categoria(1L, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1L, "TEST-1", 19.99, 100, "test1.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2L, "TEST-2", 14.99, 59, "test2.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    WebSocketHandler webSocketHandlerMock = mock (WebSocketHandler.class);
    @Mock
    private FunkosRepository funkoRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private CategoriasService categoriasService;
    @Mock
    private FunkoMapper funkoMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;
    @InjectMocks
    private FunkosServiceImpl funkosService;


    @BeforeEach
    void setUp(){
        funkosService.setWebSocketService(webSocketHandlerMock);
    }


    @Test
    void findAll(){
        // Arrange
        String categoria = "";
        List<Funko> listaFunkos = List.of(funko1, funko2);

        when(funkoRepository.findAll()).thenReturn(listaFunkos);

        // Act
        List<Funko> res = funkosService.findAll(categoria);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(2, res.size())
        );

        verify(funkoRepository, times(1)).findAll();
    }

    @Test
    void findAllByCategory(){
        // Arrange
        String categoria = "OTROS";
        List<Funko> listaFunkos = List.of(funko1, funko2);

        when(funkoRepository.findByCategoriaContainsIgnoreCase(categoria.toLowerCase())).thenReturn(listaFunkos);

        // Act
        List<Funko> res = funkosService.findAll(categoria);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(2, res.size())
        );

        verify(funkoRepository, times(1)).findByCategoriaContainsIgnoreCase(categoria.toLowerCase());
    }

    @Test
    void findById(){
        // Arrange
        Long id = 1L;

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));

        // Act
        Funko funkoEncontrado = funkosService.findById(id);

        // Assert
        assertAll(
                () -> assertNotNull(funkoEncontrado),
                () -> assertEquals(funko1, funkoEncontrado),
                () -> assertEquals(funko1.getId(), funkoEncontrado.getId()),
                () -> assertEquals(funko1.getPrecio(), funkoEncontrado.getPrecio()),
                () -> assertEquals(funko1.getImagen(), funkoEncontrado.getImagen()),
                () -> assertEquals(funko1.getCantidad(), funkoEncontrado.getCantidad()),
                () -> assertEquals(funko1.getFechaCreacion(), funkoEncontrado.getFechaCreacion()),
                () -> assertEquals(funko1.getIsActivo(), funkoEncontrado.getIsActivo()),
                () -> assertEquals(funko1.getCategoria(), funkoEncontrado.getCategoria()),
                () -> assertEquals(funko1.getFechaActualizacion(), funkoEncontrado.getFechaActualizacion()),
                () -> assertEquals(funko1.getNombre(), funkoEncontrado.getNombre())
        );

        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void findById_idNotExist(){
        // Arrange
        Long id = 99L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkosService.findById(id));
        assertEquals("Funko con id " + id +" no encontrado", res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void save() throws IOException {
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("FunkoGuardado")
                .precio(12.99)
                .imagen("funkoguardado.jpg")
                .categoria("OTROS")
                .cantidad(99)
                .build();
        Funko funko = funko2;

        when(funkoRepository.save(funko)).thenReturn(funko);
        when(categoriasService.findByNombre(funkoCreateDto.getCategoria())).thenReturn(categoria);
        when(funkoMapper.toFunko(funkoCreateDto, categoria)).thenReturn(funko);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        Funko savedFunko = funkosService.save(funkoCreateDto);

        // Assert
        assertAll(
                () -> assertNotNull(savedFunko),
                () -> assertEquals(funko, savedFunko)
        );

        verify(funkoRepository, times(1)).save(funko);
        verify(funkoMapper, times(1)).toFunko(funkoCreateDto, categoria);
        verify(categoriasService, times(1)).findByNombre(funkoCreateDto.getCategoria());
    }

    @Test
    void save_categoryNotExist(){
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("FunkoGuardado")
                .precio(12.99)
                .imagen("funkoguardado.jpg")
                .categoria("OTROS")
                .cantidad(99)
                .build();

        when(categoriasService.findByNombre(funkoCreateDto.getCategoria())).thenThrow(new CategoriaNotFound(funkoCreateDto.getCategoria()));

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> funkosService.save(funkoCreateDto));
        assertEquals("Categoría OTROS no encontrada", res.getMessage());

        verify(categoriasService, times(1)).findByNombre(funkoCreateDto.getCategoria());
    }

    @Test
    void update() throws IOException {
        // Arrange
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoActualizado")
                .precio(80.99)
                .cantidad(100)
                .categoria("OTROS")
                .imagen("funkoactualizado.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        when(funkoRepository.save(funko1)).thenReturn(funko1);
        when(funkoMapper.toFunko(funkoUpdateDto, funko1, categoria)).thenReturn(funko1);
        when(categoriasService.findByNombre(funkoUpdateDto.getCategoria())).thenReturn(categoria);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        Funko funkoActualizado = funkosService.update(id, funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertNotNull(funkoActualizado),
                () -> assertEquals(funko1, funkoActualizado)
        );

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(1)).save(funko1);
        verify(categoriasService, times(1)).findByNombre(funkoUpdateDto.getCategoria());
        verify(funkoMapper, times(1)).toFunko(funkoUpdateDto, funko1, categoria);
    }

    @Test
    void update_idNotExist(){
        // Arrange
        Long id = 99L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoActualizado")
                .precio(80.99)
                .cantidad(100)
                .categoria("OTROS")
                .imagen("funkoactualizado.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkosService.update(id, funkoUpdateDto));
        assertEquals("Funko con id " + id +" no encontrado", res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(0)).save(any(Funko.class));
    }

    @Test
    void update_categoryNotExist(){
        // Arrange
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoActualizado")
                .precio(80.99)
                .cantidad(100)
                .categoria("OTROS")
                .imagen("funkoactualizado.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        when(categoriasService.findByNombre(funkoUpdateDto.getCategoria())).thenThrow(new CategoriaNotFound(funkoUpdateDto.getCategoria()));

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> funkosService.update(id, funkoUpdateDto));
        assertEquals("Categoría OTROS no encontrada", res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(categoriasService, times(1)).findByNombre(funkoUpdateDto.getCategoria());
        verify(funkoRepository, times(0)).save(any(Funko.class));
    }

    @Test
    void deleteById() throws IOException {
        // Arrange
        Long id = 2L;

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko2));
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        funkosService.deleteById(id);

        verify(funkoRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_idNotExist(){
        // Arrange
        Long id = 99L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkosService.deleteById(id));
        assertEquals("Funko con id " + id +" no encontrado", res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(0)).deleteById(id);
    }

    @Test
    void onChange() throws IOException {
        // Arrange
        doNothing().when(webSocketHandlerMock).sendMessage(any(String.class));

        // Act
        funkosService.onChange(Notificacion.Tipo.CREATE, any(Funko.class));
    }

    @Test
    void updateImage() throws IOException {
        // Arrange
        String imageUrl = "test1.jpg";

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(funkoRepository.findById(funko1.getId())).thenReturn(Optional.of(funko1));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(funkoRepository.save(any(Funko.class))).thenReturn(funko1);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        // Act
        Funko updatedFunko = funkosService.updateImage(funko1.getId(), multipartFile);

        // Assert
        assertEquals(updatedFunko.getImagen(), imageUrl);
        verify(funkoRepository, times(1)).save(any(Funko.class));
        verify(storageService, times(1)).delete(funko1.getImagen());
        verify(storageService, times(1)).store(multipartFile);
    }
}
