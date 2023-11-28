package org.develop.users.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.develop.rest.users.dto.UserInfoResponse;
import org.develop.rest.users.dto.UserRequest;
import org.develop.rest.users.dto.UserResponse;
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
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

}
