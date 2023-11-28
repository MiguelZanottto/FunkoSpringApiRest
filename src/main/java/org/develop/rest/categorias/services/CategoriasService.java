package org.develop.rest.categorias.services;

import org.develop.rest.categorias.dto.CategoriaDto;
import org.develop.rest.categorias.models.Categoria;

import java.util.List;

public interface CategoriasService  {

    List <Categoria> findAll(String nombre);

    Categoria findByNombre(String nombre);

    Categoria findById(Long id);

    Categoria save(CategoriaDto categoriaDto);

    Categoria update(Long id, CategoriaDto categoriaDto);

    void deleteById(Long id);
}
