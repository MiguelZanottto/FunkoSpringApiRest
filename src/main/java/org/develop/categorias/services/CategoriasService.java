package org.develop.categorias.services;

import org.develop.categorias.dto.CategoriaDto;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.repositories.CategoriasRepository;

import java.util.List;

public interface CategoriasService  {

    List <Categoria> findAll(String nombre);

    Categoria findByNombre(String nombre);

    Categoria findById(Long id);

    Categoria save(CategoriaDto categoriaDto);

    Categoria update(Long id, CategoriaDto categoriaDto);

    void deleteById(Long id);
}
