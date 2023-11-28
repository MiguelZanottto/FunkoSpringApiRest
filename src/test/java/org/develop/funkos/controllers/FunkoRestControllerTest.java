package org.develop.funkos.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.funkos.dto.FunkoCreateDto;
import org.develop.rest.funkos.dto.FunkoUpdateDto;
import org.develop.rest.funkos.exceptions.FunkoNotFound;
import org.develop.rest.funkos.models.Funko;
import org.develop.rest.funkos.services.FunkosService;
import org.develop.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class FunkoRestControllerTest {
    private final String myEndpoint = "/v1/funkos";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMv;
    @MockBean
    private FunkosService funkoService;
    @Autowired
    private JacksonTester<FunkoCreateDto> jsonFunkoCreateDto;
    @Autowired
    private JacksonTester <FunkoUpdateDto> jsonFunkoUpdateDto;
    private final Categoria categoria = new Categoria(1L, "OTROS", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1L, "TEST-1", 19.99, 100, "test1.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2L, "TEST-2", 14.99, 59, "test2.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

    @Autowired
    public FunkoRestControllerTest(FunkosService funkoService){
        this.funkoService = funkoService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllFunkos() throws Exception {
        List<Funko> listaFunkos = List.of(funko1, funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkos_ByNombre() throws Exception {
        List<Funko> listaFunkos = List.of(funko1);
        String localEndPoint = myEndpoint + "?nombre=TEST-1";

        Optional<String> nombre = Optional.of("TEST-1");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkos_ByCategoria() throws Exception {
        List<Funko> listaFunkos = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?categoria=OTROS";

        Optional<String> categoria = Optional.of("OTROS");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }


    @Test
    void getAllFunkos_ByPrecio() throws Exception {
        List<Funko> listaFunkos = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?precioMax=20.00";

        Optional<Double> precioMax = Optional.of(20.00);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), precioMax, Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), precioMax, Optional.empty(), Optional.empty(), pageable);
    }



    @Test
    void getAllFunkos_ByCantidad() throws Exception {
        List<Funko> listaFunkos = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?cantidadMin=100";

        Optional<Integer> cantidadMin = Optional.of(100);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), cantidadMin, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), cantidadMin, Optional.empty(), pageable);
    }

    @Test
    void getAllFunkos_ByIsActivo() throws Exception {
        List<Funko> listaFunkos = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?isActivo=true";

        Optional<Boolean> isActivo = Optional.of(true);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), isActivo, pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), isActivo, pageable);
    }


    @Test
    void getFunkoById() throws Exception {
        // Arrange
        String localEndPoint = myEndpoint + "/1";

        when(funkoService.findById(1L)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1, res)
        );
    }

    @Test
    void getFunkoById_idNotExists() throws Exception {
        // Arrange
        String localEndPoint = myEndpoint + "/1";

        when(funkoService.findById(1L)).thenThrow(new FunkoNotFound(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // verify
        verify(funkoService, times(1)).findById(1L);
    }


    @Test
    void createFunko() throws Exception{
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("DISNEY")
                .build();

        when(funkoService.save(funkoDto)).thenReturn(funko1);

        MockHttpServletResponse response = mockMv.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(funko1, res)
        );

        // Verify
        verify(funkoService, times(1)).save(funkoDto);
    }

    @Test
    void createFunko_BadRequest_Nombre() throws Exception {
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("Fu")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("DISNEY")
                .build();

        MockHttpServletResponse response = mockMv.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe contener al menos 3 letras"))
        );
    }

    @Test
    void createFunko_BadRequest_Precio() throws Exception {
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("FunkoTest3")
                .precio(-16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("DISNEY")
                .build();

        MockHttpServletResponse response = mockMv.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio no puede ser negativo"))
        );
    }

    @Test
    void createFunko_BadRequest_Cantidad() throws Exception {
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(-9)
                .imagen("funkotest3.jpg")
                .categoria("DISNEY")
                .build();

        MockHttpServletResponse response = mockMv.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La cantidad no puede ser negativa"))
        );
    }



    @Test
    void createFunko_BadRequest_Categoria() throws Exception {
        // Arrange
        FunkoCreateDto funkoDto =  FunkoCreateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("HARRYPOTTER")
                .build();

        MockHttpServletResponse response = mockMv.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS"))
        );
    }


    @Test
    void updateFunko() throws Exception {
        // Arrange
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko2);

        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko2, res)
        );

        // Verify
        verify(funkoService, times(1)).update(1L, funkoUpdateDto);
    }

    @Test
    void updateFunkoNotFound() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenThrow(new FunkoNotFound(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    void updateFunko_BadRequest_Nombre() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("F")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe contener al menos 3 letras"))
        );
    }


    @Test
    void updateFunko_BadRequest_Precio() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(-16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio no puede ser negativo"))
        );
    }



    @Test
    void updateFunko_BadRequest_Cantidad() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(-9)
                .imagen("funkotest3.jpg")
                .categoria("OTROS")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La cantidad no puede ser negativa"))
        );
    }



    @Test
    void updateFunko_BadRequest_Categoria() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoTest3")
                .precio(16.99)
                .cantidad(9)
                .imagen("funkotest3.jpg")
                .categoria("PEPITO'S")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS"))
        );
    }

    @Test
    void updatePartialFunko() throws Exception {
        String myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre(null)
                .precio(16.99)
                .cantidad(9)
                .imagen(null)
                .categoria(null)
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        patch(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoUpdateDto.write(funkoUpdateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1, res)
        );

        // Verify
        verify(funkoService, times(1)).update(1L, funkoUpdateDto);
    }

    @Test
    void deleteFunkoById() throws Exception {
        // Arrange
        String myLocalEndpoint = myEndpoint + "/1";

        doNothing().when(funkoService).deleteById(1L);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(() -> assertEquals(204, response.getStatus()));

        // Verify
        verify(funkoService, times(1)).deleteById(1L);
    }

    @Test
    void deleteFunkoById_IdNotExist() throws Exception {
        // Arrange
        String myLocalEndpoint = myEndpoint + "/1";

        doThrow(new FunkoNotFound(1L)).when(funkoService).deleteById(1L);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(funkoService, times(1)).deleteById(1L);
    }





    @Test
    void updateFunkoImage() throws Exception {
        var myLocalEndpoint = myEndpoint + "/imagen/1";

        when(funkoService.updateImage(anyLong(), any(MultipartFile.class))).thenReturn(funko1);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido del archivo".getBytes()
        );

        MockHttpServletResponse response = mockMv.perform(
                multipart(myLocalEndpoint)
                        .file(file)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
        ).andReturn().getResponse();


        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1, res)
        );

        // Verify
        verify(funkoService, times(1)).updateImage(anyLong(), any(MultipartFile.class));
    }

}
