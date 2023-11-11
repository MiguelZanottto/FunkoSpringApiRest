package org.develop.funkos.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.models.Funko;
import org.develop.funkos.services.FunkosService;
import org.develop.utils.pageresponse.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("${api.version}/funkos") // Es la ruta del controlador
public class FunkoRestController {
    private  final FunkosService funkosService;

    @Autowired
    public FunkoRestController(FunkosService funkosService) {
        this.funkosService = funkosService;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Funko>> getAllProducts(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<Double> precioMax,
            @RequestParam(required = false) Optional<Integer> cantidadMin,
            @RequestParam(required = false) Optional<Boolean> isActivo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Buscando todos los productos con las siguientes opciones: " + nombre + " " + categoria + " " + precioMax + " " + cantidadMin + " " + isActivo);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
             Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(funkosService.findAll(nombre, categoria, precioMax, cantidadMin, isActivo, pageable), sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity <Funko> getFunkoById(@PathVariable Long id){
        log.info("Buscando funko por id: " + id);
        return ResponseEntity.ok(funkosService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Funko> createFunko(@Valid @RequestBody FunkoCreateDto funkoCreateDto){
        log.info("Creando funko: " + funkoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(funkosService.save(funkoCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funko> updateFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funkoUpdateDto){
        log.info("Actualizando funko por id: "+ id + " con funko: " + funkoUpdateDto);
        return ResponseEntity.ok(funkosService.update(id, funkoUpdateDto));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Funko> updatePartialFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funkoUpdateDto){
        log.info("Actualizando funko por id: "+ id + " con funko: " + funkoUpdateDto);
        return ResponseEntity.ok(funkosService.update(id, funkoUpdateDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id){
        log.info("Borrando funko por id: " + id);
        funkosService.deleteById(id);
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


    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> nuevoFunko(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        log.info("Actualizando imagen de producto por id: " + id);

        if (!file.isEmpty()) {
            // Actualizamos el funko
            return ResponseEntity.ok(funkosService.updateImage(id, file));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el funko o esta está vacía");
        }
    }

}
