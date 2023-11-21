package org.develop.funkos.controllers;

import org.develop.categorias.models.Categoria;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.exceptions.FunkoNotFound;
import org.develop.funkos.models.Funko;
import org.develop.funkos.services.FunkosService;
import org.develop.utils.pageresponse.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoRestControllerNotMvcTest {
    @Mock
    private FunkosService funkoService;
    @InjectMocks
    private FunkoRestController funkoController;
    private final Categoria categoria = new Categoria(1L, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1L, "TEST-1", 19.99, 100, "test1.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2L, "TEST-2", 14.99, 59, "test2.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

    @Test
    void getAllProducts() {
        List<Funko> expectedFunks = List.of(funko1, funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(expectedFunks);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),  pageable)).thenReturn(page);


        ResponseEntity<PageResponse<Funko>> responseEntity  = funkoController.getAllFunks(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0 , 10, "id", "asc");

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().content().size());

        // Verify
        verify(funkoService, times(1)).findAll( Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunks_ByName() {
        Optional<String> nombre = Optional.of("TEST-2");
        List<Funko> expectedFunks = List.of(funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(expectedFunks);

        // Arrange
        when(funkoService.findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),  pageable)).thenReturn(page);


        ResponseEntity<PageResponse<Funko>> responseEntity  = funkoController.getAllFunks(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0 , 10, "id", "asc");

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().content().size());

        // Verify
        verify(funkoService, times(1)).findAll( nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }


    @Test
    void getFunkById(){
        // Arrange
        Long id = funko2.getId();

        when(funkoService.findById(id)).thenReturn(funko2);

        ResponseEntity<Funko> responseEntity = funkoController.getFunkoById(id);

        // Assert
        assertAll(
                () -> assertEquals(200, responseEntity.getStatusCode().value()),
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(funko2, responseEntity.getBody())
        );

        // verify
        verify(funkoService, times(1)).findById(id);
    }


    @Test
    void getFunkById_idNotExists() {
        // Arrange
        Long id = 100L;

        when(funkoService.findById(id)).thenThrow(new FunkoNotFound(id));

        // Act
        var result = assertThrows(FunkoNotFound.class, () -> funkoController.getFunkoById(id));
        assertEquals("Funko con id 100 no encontrado", result.getMessage());

        // Verify
        verify(funkoService, times(1)).findById(id);
    }

    @Test
    void createFunko() {
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("DISNEY")
                .build();

        when(funkoService.save(funkoDto)).thenReturn(funko1);

        // Act
        ResponseEntity<Funko> result = funkoController.createFunko(funkoDto);

        // Assert
        assertAll(
                () -> assertEquals(201, result.getStatusCode().value()),
                () -> assertEquals(funko1, result.getBody())
        );

        // Verify
        verify(funkoService, times(1)).save(funkoDto);
    }



    @Test
    void updateFunko()  {
        // Arrange
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        when(funkoService.update(2L, funkoUpdateDto)).thenReturn(funko2);

        // Act
        ResponseEntity<Funko> result = funkoController.updateFunko(funko2.getId(), funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertEquals(200, result.getStatusCode().value()),
                () -> assertEquals(funko2, result.getBody())
        );

        // Verify
        verify(funkoService, times(1)).update(2L, funkoUpdateDto);
    }


    @Test
    void updateFunko_NotFound(){
        // Arrange
        when(funkoService.update(anyLong(), any())).thenThrow(new FunkoNotFound(10L));

        // Act & Assert
        var result = assertThrows(FunkoNotFound.class, () -> funkoService.update(anyLong(), any()));
        assertEquals("Funko con id 10 no encontrado", result.getMessage());

        // Verify
        verify(funkoService, times(1)).update(anyLong(), any());
    }


    @Test
    void updatePartialFunko(){
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre(null)
                .precio(16.99)
                .cantidad(9)
                .imagen(null)
                .categoria(null)
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        ResponseEntity<Funko> responseEntity = funkoController.updateFunko(1L, funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertEquals(200, responseEntity.getStatusCode().value()),
                () -> assertEquals(funko1, responseEntity.getBody())
        );

        // Verify
        verify(funkoService, times(1)).update(1L, funkoUpdateDto);
    }

    @Test
    void deleteFunkById()  {
        // Arrange
        doNothing().when(funkoService).deleteById(any());

        // Act
        ResponseEntity<Void> responseEntity = funkoController.deleteFunko(any());

        // Assert
        assertAll(() -> assertEquals(204, responseEntity.getStatusCode().value()));

        // Verify
        verify(funkoService, times(1)).deleteById(any());
    }

    @Test
    void deleteFunkById_NotFound()  {
        // Arrange
        doThrow(new FunkoNotFound(1L)).when(funkoService).deleteById(any());

        // Act
        var result = assertThrows(FunkoNotFound.class, () -> funkoController.deleteFunko(any()));
        assertEquals("Funko con id 1 no encontrado", result.getMessage());

        // Verify
        verify(funkoService, times(1)).deleteById(any());
    }



    @Test
    void updateFunkoImage()  {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido del archivo".getBytes()
        );

        when(funkoService.updateImage(1L, file)).thenReturn(funko1);

        ResponseEntity<Funko> result = funkoController.updateImage(1L, file);

        // Assert
        assertAll(
                () -> assertEquals(200, result.getStatusCode().value()),
                () -> assertEquals(funko1, result.getBody())
        );

        // Verify
        verify(funkoService, times(1)).updateImage(1L, file);
    }


    @Test
    void updateImage_InvalidFile() {
        // Arrange
        Long id = funko1.getId();

        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "filename.jpg",
                "image/jpeg",
                new byte[0]
        );

        var exception = assertThrows(ResponseStatusException.class, () -> funkoController.updateImage(id, invalidFile));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("400 BAD_REQUEST \"No se ha enviado una imagen para el funko o esta está vacía\"", exception.getMessage());
    }
}
