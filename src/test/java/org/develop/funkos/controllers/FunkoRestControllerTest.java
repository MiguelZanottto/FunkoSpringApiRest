package org.develop.funkos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.develop.categorias.models.Categoria;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.exceptions.FunkoNotFound;
import org.develop.funkos.models.Funko;
import org.develop.funkos.services.FunkosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
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

        // Arrange
        when(funkoService.findAll(null)).thenReturn(listaFunkos);

        MockHttpServletResponse response = mockMv.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.size()),
                () -> assertEquals(funko1, res.get(0)),
                () -> assertEquals(funko2, res.get(1))
        );

        // Verify
        verify(funkoService, times(1)).findAll(null);
    }

    @Test
    void getAllFunkosByCategory() throws Exception {
        // Arrange
        List <Funko> listaFunkos = List.of(funko1);
        String localEndPoint = myEndpoint + "?categoria=OTROS";

        when(funkoService.findAll("OTROS")).thenReturn(listaFunkos);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMv.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(funko1, res.get(0))
        );
        verify(funkoService, times(1)).findAll("OTROS");
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

}
