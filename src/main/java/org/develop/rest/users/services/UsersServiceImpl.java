package org.develop.rest.users.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.develop.rest.pedidos.repositories.PedidoRepository;
import org.develop.rest.users.dto.UserInfoResponse;
import org.develop.rest.users.dto.UserRequest;
import org.develop.rest.users.dto.UserResponse;
import org.develop.rest.users.exceptions.UserNameOrEmailExists;
import org.develop.rest.users.exceptions.UserNotFound;
import org.develop.rest.users.mappers.UsersMapper;
import org.develop.rest.users.models.User;
import org.develop.rest.users.repositories.UsersRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PedidoRepository pedidoRepository;
    private final UsersMapper usersMapper;

    public UsersServiceImpl(UsersRepository usersRepository, PedidoRepository pedidoRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.pedidoRepository = pedidoRepository;
        this.usersMapper = usersMapper;
    }

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: " + username + " y borrados: " + isDeleted);
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.where(specUsernameUser)
                .and(specEmailUser)
                .and(specIsDeleted);

        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoResponse findById(Long id) {
        log.info("Buscando usuario por id: " + id);
        var user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        var pedidos = pedidoRepository.findPedidosIdsByIdUsuario(id).stream().map(p -> p.getId().toHexString()).toList();
        return usersMapper.toUserInfoResponse(user, pedidos);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: " + userRequest);
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: " + userRequest);
        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("usuario encontrado: " + u.getId() + " Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: " + id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        if (pedidoRepository.existsByIdUsuario(id)) {
            log.info("Borrado lógico de usuario por id: " + id);
            usersRepository.updateIsDeletedToTrueById(id);
        } else {
            log.info("Borrado físico de usuario por id: " + id);
            usersRepository.delete(user);
        }
    }
}
