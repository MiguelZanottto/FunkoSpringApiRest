package org.develop.users.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.develop.rest.users.dto.UserInfoResponse;
import org.develop.rest.users.dto.UserRequest;
import org.develop.rest.users.dto.UserResponse;
import org.develop.rest.users.exceptions.UserNameOrEmailExists;
import org.develop.rest.users.exceptions.UserNotFound;
import org.develop.rest.users.models.User;
import org.develop.rest.users.services.UsersService;
import org.develop.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class UsersRestControllerTest {

    private final UserRequest userRequest = UserRequest.builder()
            .nombre("test perez")
            .apellidos("test teando")
            .password("password_test")
            .username("username_test")
            .email("test@test.com")
            .build();

    private final User user = User.builder()
            .id(50L)
            .nombre("test perez")
            .apellidos("test teando")
            .password("password_test")
            .username("username_test")
            .email("test@test.com")
            .build();

    private final UserResponse userResponse = UserResponse.builder()
            .id(50L)
            .nombre("test perez")
            .apellidos("test teando")
            .username("username_test")
            .email("test@test.com")
            .build();

    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(50L)
            .nombre("test perez")
            .apellidos("test teando")
            .username("username_test")
            .email("test@test.com")
            .build();

    private final String myEndPoint = "/v1/users";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UsersService userService;

    @Autowired
    public UsersRestControllerTest(UsersService userService){
        this.userService = userService;
        mapper.registerModule(new JavaTimeModule());
    }

   @Test
   @WithAnonymousUser
   void authenticate_false() throws Exception {
       MockHttpServletResponse response = mockMvc.perform(
               get(myEndPoint).accept(MediaType.APPLICATION_JSON)
                       .contentType(MediaType.APPLICATION_JSON))
                       .andReturn().getResponse();

       assertEquals(403, response.getStatus());
   }

    @Test
    void findAllUsers() throws Exception {
        var lista = List.of(userResponse);
        Page<UserResponse> page = new PageImpl<>(lista);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(userService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndPoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<UserResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(userService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
   }

    @Test
    void findById() throws Exception {
        String myLocalEndPoint = myEndPoint + "/1";

        when(userService.findById(1L)).thenReturn(userInfoResponse);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var result = mapper.readValue(response.getContentAsString(), UserInfoResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userInfoResponse, result)
        );

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void findById_notFound() throws Exception {
        String myLocalEndPoint = myEndPoint + "/100";
        Long id = 100L;

        when(userService.findById(id)).thenThrow(new UserNotFound("No existe el usuario con id " + id));

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
        verify(userService, times(1)).findById(id);
    }


    @Test
    void createUser() throws Exception {
        when(userService.save(userRequest)).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndPoint).accept(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );

        verify(userService, times(1)).save(userRequest);
    }

    @Test
    void createUser_false_UserNameOrEmailExists() throws Exception {
        when(userService.save(userRequest)).thenThrow(new UserNameOrEmailExists("El nombre de usuario o el email ya existen"));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint).accept(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(userService, times(1)).save(userRequest);
    }
    @Test
    void createUser_false_BadRequest() throws Exception {
        UserRequest falseUserRequest = UserRequest.builder()
                    .nombre("")
                    .apellidos("")
                    .email("test@test.com")
                    .password("test")
                    .username("")
                    .build();

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint).accept(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(falseUserRequest)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Apellidos no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Username no puede estar"))
        );
    }

    @Test
    void updateUser() throws Exception {
        String myLocalEndPoint = myEndPoint + "/1";

        when(userService.update(1L, userRequest)).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var result = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userResponse, result)
        );

        verify(userService, times(1)).update(1L, userRequest);

    }

    @Test
    void deleteUser() throws Exception {
        String myLocalEndPoint = myEndPoint + "/1";

        doNothing().when(userService).deleteById(1L);

        MockHttpServletResponse response = mockMvc.perform(
                delete(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    @WithUserDetails("admin")
    void testMe() throws Exception {
        String myLocalEndPoint = myEndPoint + "/me/profile";

        when(userService.findById(1L)).thenReturn(userInfoResponse);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testMe_false() throws Exception {
        String myLocalEndPoint = myEndPoint + "/me/profile";

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }


    @Test
    @WithUserDetails("test")
    void deleteMe_true() throws Exception{
        String myLocalEndPoint = myEndPoint + "/me/profile";

        doNothing().when(userService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    @WithUserDetails("test")
    void updateMe_true() throws Exception {
        String myLocalEndPoint = myEndPoint + "/me/profile";

        when(userService.update(3L, userRequest)).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var result = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userResponse, result)
        );

        verify(userService, times(1)).update(3L, userRequest);

    }
}
