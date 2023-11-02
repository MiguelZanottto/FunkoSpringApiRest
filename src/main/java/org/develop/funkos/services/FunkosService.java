package org.develop.funkos.services;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.models.Funko;

import java.util.List;

public interface FunkosService {
    List<Funko> findAll(String categoria);
    Funko findById(Long id);
    Funko save(FunkoCreateDto funkoCreateDto);
    Funko update(Long id, FunkoUpdateDto funkoUpdateDto);
    void deleteById(Long id);
}

