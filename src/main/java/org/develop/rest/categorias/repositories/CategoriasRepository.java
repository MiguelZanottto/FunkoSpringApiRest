package org.develop.rest.categorias.repositories;

import org.develop.rest.categorias.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriasRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreEqualsIgnoreCase(String nombre);
    Optional<Categoria> findByNombreEqualsIgnoreCaseAndIsActivoTrue(String nombre);
    List<Categoria> findAllByNombreContainingIgnoreCase(String nombre);
    List<Categoria> findAllByNombreContainingIgnoreCaseAndIsActivoTrue(String nombre);
    List<Categoria> findByIsActivo(Boolean isActiva);

    @Modifying
    @Query("UPDATE Categoria c SET c.isActivo = false WHERE c.id = :id")

    void updateIsActivoFalseById(Long id);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Funko f WHERE f.categoria.id = :id")
    Boolean existsFunkoById(Long id);
}
