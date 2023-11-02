package org.develop.funkos.repositories;

import org.develop.categorias.models.Categoria;
import org.develop.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface FunkosRepository  extends JpaRepository<Funko, Long> {
    @Query("SELECT f FROM Funko f WHERE LOWER(f.categoria.nombre) LIKE LOWER(:categoria)")
    List<Funko> findByCategoriaContainsIgnoreCase(String categoria);

    @Query("SELECT f FROM Funko f WHERE LOWER(f.categoria.nombre) LIKE %:categoria% AND f.isActivo = true")
    List<Funko> findByCategoriaContainsIgnoreCaseAndIsActivoTrue(String categoria);

    List<Funko> findByIsActivo(Boolean isActivo);

    @Modifying
    @Query("UPDATE Funko f SET f.isActivo = false WHERE f.id = :id")
    void updateIsActivoToFalseById(Long id);
}
