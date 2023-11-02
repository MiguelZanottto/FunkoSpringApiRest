package org.develop.funkos.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.models.Funko;
import org.develop.funkos.services.FunkosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<Funko>> getAllFunkos(@RequestParam(required = false) String categoria){
        log.info("Buscando todos los funkos con categoria: " + categoria);
        return ResponseEntity.ok(funkosService.findAll(categoria));
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

}
