package org.develop.rest.categorias.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.rest.categorias.dto.CategoriaDto;
import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.categorias.services.CategoriasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${api.version}/categorias")
@PreAuthorize("hasRole('USER')")
public class CategoriasRestControllers {
    private final CategoriasService categoriasService;

    @Autowired
    public CategoriasRestControllers(CategoriasService categoriasService) {
        this.categoriasService = categoriasService;
    }

    @GetMapping()
    public ResponseEntity<List<Categoria>> getAllCategorias(
            @RequestParam (required = false) String nombre)
    {
        log.info("Buscando todas las categorias con nombre: " + nombre);
        return ResponseEntity.ok(categoriasService.findAll(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity <Categoria> getCategoriaById(@PathVariable Long id){
        log.info("Buscando la categoria con id: " + id);
        return ResponseEntity.ok(categoriasService.findById(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> createCategory(@Valid @RequestBody CategoriaDto categoriaCreateDto) {
        log.info("Creando categegoría: " + categoriaCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriasService.save(categoriaCreateDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoriaDto categoriaUpdateDto) {
        log.info("Actualizando categoria por id: " + id + " con categoria: " + categoriaUpdateDto);
        return ResponseEntity.ok(categoriasService.update(id, categoriaUpdateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Borrando categoria por id: " + id);
        categoriasService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
