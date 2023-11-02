package org.develop.categorias.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.develop.categorias.exceptions.CategoriaConflict;
import org.develop.categorias.exceptions.CategoriaNotSave;
import org.develop.categorias.mappers.CategoriasMapper;
import org.develop.categorias.dto.CategoriaDto;
import org.develop.categorias.exceptions.CategoriaNotFound;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.repositories.CategoriasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriasServiceImpl implements CategoriasService{
    private final CategoriasRepository categoriasRepository;
    private final CategoriasMapper categoriasMapper;

    @Autowired
    public CategoriasServiceImpl(CategoriasRepository categoriasRepository, CategoriasMapper categoriasMapper) {
        this.categoriasRepository = categoriasRepository;
        this.categoriasMapper = categoriasMapper;
    }


    @Override
    public List<Categoria> findAll(String nombre) {
        log.info("Buscando categorias por nombre: " + nombre);
        if(nombre == null || nombre.isEmpty()){
            return categoriasRepository.findAll();
        } else {
            return categoriasRepository.findAllByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    @Cacheable
    public Categoria findByNombre(String nombre) {
        log.info("Buscando categoria por su nombre: " + nombre);
        return categoriasRepository.findByNombreEqualsIgnoreCase(nombre).orElseThrow(() -> new CategoriaNotFound(nombre));
    }

    @Override
    @Cacheable
    public Categoria findById(Long id) {
        log.info("Buscando categoria por su id: " + id);
        return categoriasRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    @CachePut
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Guardando categoría: " + categoriaDto);
        if(categoriasRepository.findByNombreEqualsIgnoreCase(categoriaDto.getNombre()).isPresent()){
            throw new CategoriaNotSave(categoriaDto.getNombre());
        }
        return categoriasRepository.save(categoriasMapper.toCategoria(categoriaDto));
    }


    @Override
    @CachePut
    public Categoria update(Long id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoria: " + categoriaDto);
        Categoria categoriaActual = findById(id);
        return categoriasRepository.save(categoriasMapper.toCategoria(categoriaDto, categoriaActual));
    }

    @Override
    @CacheEvict
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando categoría con id: " + id);
        Categoria categoria = findById(id);
        if(categoriasRepository.existsFunkoById(id)){
            log.warn("No se pude borrar la categoria con id: " + id + " porque tiene funkos asociados");
            throw new CategoriaConflict("No se puede borrar la categoría con id " + id + " porque tiene funkos asociados");
        } else {
            categoriasRepository.deleteById(id);
        }
    }
}
