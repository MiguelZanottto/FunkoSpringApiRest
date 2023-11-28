package org.develop.users.services;

import org.develop.rest.pedidos.repositories.PedidoRepository;
import org.develop.rest.users.dto.UserInfoResponse;
import org.develop.rest.users.dto.UserRequest;
import org.develop.rest.users.dto.UserResponse;
import org.develop.rest.users.exceptions.UserNameOrEmailExists;
import org.develop.rest.users.exceptions.UserNotFound;
import org.develop.rest.users.mappers.UsersMapper;
import org.develop.rest.users.models.User;
import org.develop.rest.users.repositories.UsersRepository;
import org.develop.rest.users.services.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTest {
    private final UserRequest userRequest = UserRequest.builder().username("test").email("test@pruebita.com").build();
    private final User user = User.builder().id(99L).username("test").email("test@pruebita.com").build();
    private final UserResponse userResponse = UserResponse.builder().username("test").email("test@pruebita.com").build();
    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder().username("test").email("test@pruebita.com").build();
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsersMapper usersMapper;
    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void findAll_NotFilters(){
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        Page<User> page = new PageImpl<>(users);

        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(usersMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse());

        Page <UserResponse> response = usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(2, response.getTotalElements())
        );

        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findById(){
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidoRepository.findPedidosIdsByIdUsuario(userId)).thenReturn(List.of());
        when(usersMapper.toUserInfoResponse(any(User.class), anyList())).thenReturn(userInfoResponse);

        UserInfoResponse response = usersService.findById(userId);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(userResponse.getUsername(), response.getUsername()),
                () -> assertEquals(userResponse.getEmail(), response.getEmail())
        );

        verify(usersRepository, times(1)).findById(userId);
        verify(pedidoRepository, times(1)).findPedidosIdsByIdUsuario(userId);
        verify(usersMapper, times(1)).toUserInfoResponse(user, List.of());
    }

    @Test
    void findById_NotFound(){
        Long userId = 100L;

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        var result = assertThrows(UserNotFound.class, () -> usersService.findById(userId));
        assertEquals("Usuario con id " + userId + " no encontrado", result.getMessage());

        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void save_true(){
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class))).thenReturn(Optional.empty());
        when(usersMapper.toUser(userRequest)).thenReturn(user);
        when(usersMapper.toUserResponse(user)).thenReturn(userResponse);
        when(usersRepository.save(user)).thenReturn(user);

        UserResponse response = usersService.save(userRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(userRequest.getUsername(), response.getUsername()),
                () -> assertEquals(userRequest.getEmail(), response.getEmail())
        );

        verify(usersMapper, times(1)).toUser(userRequest);
        verify(usersMapper, times(1)).toUserResponse(user);
        verify(usersRepository, times(1)).findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class));
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void save_false_usernameOrEmailExists(){
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class))).thenReturn(Optional.of(new User()));

        assertThrows(UserNameOrEmailExists.class, () -> usersService.save(userRequest));
    }

    @Test
    void update_true(){
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class))).thenReturn(Optional.empty());
        when(usersMapper.toUser(userRequest, userId)).thenReturn(user);
        when(usersMapper.toUserResponse(user)).thenReturn(userResponse);
        when(usersRepository.save(user)).thenReturn(user);

        UserResponse response = usersService.update(userId, userRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(userRequest.getUsername(), response.getUsername()),
                () -> assertEquals(userRequest.getEmail(), response.getEmail())
        );

        verify(usersMapper, times(1)).toUserResponse(user);
        verify(usersRepository, times(1)).save(user);
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class));
        verify(usersMapper, times(1)).toUser(userRequest, userId);
    }

    @Test
    void update_false_UsernameOrEmailExist(){
        Long userId = 5L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(any(String.class), any(String.class))).thenReturn(Optional.of(user));

        assertThrows(UserNameOrEmailExists.class, () -> usersService.update(userId, userRequest));
    }

    @Test
    void update_false_userNotFound(){
        Long userId = 100L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        var result = assertThrows(UserNotFound.class, () -> usersService.update(userId, userRequest));
        assertEquals("Usuario con id " + userId + " no encontrado", result.getMessage());

        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void deleteById_phisical(){
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidoRepository.existsByIdUsuario(userId)).thenReturn(false);

        usersService.deleteById(userId);

        verify(usersRepository, times(1)).findById(userId);
        verify(pedidoRepository, times(1)).existsByIdUsuario(userId);
    }

    @Test
    void deleteById_logical(){
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidoRepository.existsByIdUsuario(userId)).thenReturn(true);
        doNothing().when(usersRepository).updateIsDeletedToTrueById(userId);

        usersService.deleteById(userId);

        verify(usersRepository, times(1)).updateIsDeletedToTrueById(userId);
        verify(pedidoRepository, times(1)).existsByIdUsuario(userId);
    }

    @Test
    void deleteById_false_userNotFound(){
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        var result = assertThrows(UserNotFound.class, () -> usersService.deleteById(userId));
        assertEquals("Usuario con id " + userId + " no encontrado", result.getMessage());
    }
}
