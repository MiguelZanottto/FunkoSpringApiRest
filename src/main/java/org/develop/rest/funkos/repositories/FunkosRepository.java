package org.develop.rest.funkos.repositories;

import org.develop.rest.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface FunkosRepository  extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {
    List<Funko> findByIsActivo(Boolean isActivo);

    @Modifying
    @Query("UPDATE Funko f SET f.isActivo = false WHERE f.id = :id")
    void updateIsActivoToFalseById(Long id);
}
