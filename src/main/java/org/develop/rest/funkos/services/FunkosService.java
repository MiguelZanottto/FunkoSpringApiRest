package org.develop.rest.funkos.services;
import org.develop.rest.funkos.dto.FunkoCreateDto;
import org.develop.rest.funkos.dto.FunkoUpdateDto;
import org.develop.rest.funkos.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FunkosService {
    Page<Funko> findAll(Optional<String> nombre, Optional <String> categoria, Optional<Double> precioMax, Optional<Integer> cantidadMin, Optional<Boolean> isActivo, Pageable pageable);
    Funko findById(Long id);
    Funko save(FunkoCreateDto funkoCreateDto);
    Funko update(Long id, FunkoUpdateDto funkoUpdateDto);
    void deleteById(Long id);
    Funko updateImage(Long id, MultipartFile image);
}

