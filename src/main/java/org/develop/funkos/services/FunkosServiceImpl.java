package org.develop.funkos.services;

import lombok.extern.slf4j.Slf4j;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.services.CategoriasService;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.exceptions.FunkoNotFound;
import org.develop.funkos.mappers.FunkoMapper;
import org.develop.funkos.models.Funko;
import org.develop.funkos.repositories.FunkosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FunkosServiceImpl implements FunkosService {
    private final FunkosRepository funkosRepository;
    private final CategoriasService categoriasService;
    private final FunkoMapper funkoMapper;

    @Autowired
    public FunkosServiceImpl(FunkosRepository funkosRepository, CategoriasService categoriasService, FunkoMapper funkoMapper) {
        this.funkosRepository = funkosRepository;
        this.categoriasService = categoriasService;
        this.funkoMapper = funkoMapper;
    }


    @Override
    public List<Funko> findAll(String categoria) {
       if(categoria == null || categoria.isEmpty()){
           log.info("Buscado todos los funkos");
           return funkosRepository.findAll();
       }
       log.info("Buscando todos los funkos por categoria: " + categoria);
       return funkosRepository.findByCategoriaContainsIgnoreCase(categoria.toLowerCase());
    }

    @Override
    public Funko findById(Long id) {
        log.info("Buscando funko por id: " + id);
        return funkosRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
    }

    @Override
    public Funko save(FunkoCreateDto funkoCreateDto) {
        log.info("Guardando funko: " + funkoCreateDto);
        Categoria categoria = categoriasService.findByNombre(funkoCreateDto.getCategoria());
        return funkosRepository.save(funkoMapper.toFunko(funkoCreateDto, categoria));
    }

    @Override
    public Funko update(Long id, FunkoUpdateDto funkoUpdateDto) {
        log.info("Actualizando funko por id: " + id);
        Funko funkoActual = this.findById(id);
        Categoria categoria = null;
        if(funkoUpdateDto.getCategoria() != null && !funkoUpdateDto.getCategoria().isEmpty()){
            categoria = categoriasService.findByNombre(funkoUpdateDto.getCategoria());
        } else {
            categoria = funkoActual.getCategoria();
        }
        return funkosRepository.save(funkoMapper.toFunko(funkoUpdateDto, funkoActual, categoria));
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Borrando funko por id: " + id);
        this.findById(id);
        funkosRepository.deleteById(id);
    }
}
