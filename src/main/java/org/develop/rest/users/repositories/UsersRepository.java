package org.develop.rest.users.repositories;

import org.develop.rest.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameEqualsIgnoreCase(String username);

    Optional<User> findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    List<User> findAllByUsernameContainingIgnoreCase(String username);

    @Modifying
    @Query("UPDATE User p SET p.isDeleted = true WHERE p.id = :id")

    void updateIsDeletedToTrueById(Long id);

}
