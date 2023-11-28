package org.develop.auth.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.develop.rest.auth.dto.JwtAuthResponse;
import org.develop.rest.auth.dto.UserSignInRequest;
import org.develop.rest.auth.dto.UserSignUpRequest;
import org.develop.rest.auth.exceptions.AuthSingInInvalid;
import org.develop.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import org.develop.rest.auth.exceptions.UserDiferentePasswords;
import org.develop.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {
    private final String myEndpoint = "/v1/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthenticationService authService;

    @Autowired
    public AuthRestControllerTest(AuthenticationService authService) {
        this.authService = authService;
        mapper.registerModule(new JavaTimeModule());
    }


    @Test
    void signUp_true() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test1", "Test Teando", "test", "test@test.com", "password_test", "password_test");
        var jwtAuthResponse = new JwtAuthResponse("token");
        var myLocalEndpoint = myEndpoint + "/signup";

        when(authService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_false_DiferentsPasswords() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("username_test");
        request.setPassword("password_test");
        request.setPasswordComprobacion("contrasena_test");
        request.setEmail("test@test.com");
        request.setNombre("nombre_test");
        request.setApellidos("apellido_test");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentePasswords("Las contraseñas no coinciden"));

        assertThrows(UserDiferentePasswords.class, () -> authService.signUp(request));

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_false_usernameOrEmailExist() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("username_test");
        request.setPassword("password_test");
        request.setPasswordComprobacion("password_test");
        request.setEmail("test@test.com");
        request.setNombre("nombre_test");
        request.setApellidos("apellido_test");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe"));

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authService.signUp(request));

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }


    @Test
    void signUp_false_EmptyFields() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";

        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("password_test");
        request.setPasswordComprobacion("password_test");
        request.setEmail("");
        request.setNombre("");
        request.setApellidos("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Apellidos no puede ")),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
        );
    }

    @Test
    void signIn_true() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test1", "Test Teando", "test", "test@test.com", "password_test", "password_test");
        var jwtAuthResponse = new JwtAuthResponse("token");

        var myLocalEndpoint = myEndpoint + "/signin";

        when(authService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        // Verify
        verify(authService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signIn_false_IncorrectUsernameOrPassword() {
        var myLocalEndpoint = myEndpoint + "/signin";

        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("username_falso");
        request.setPassword("password_falso");

        when(authService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        assertThrows(AuthSingInInvalid.class, () -> authService.signIn(request));

        verify(authService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signIn_false_EmptyUsernameAndPassword() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
                //() -> assertTrue(response.getContentAsString().contains("Password no puede"))
        );
    }
}