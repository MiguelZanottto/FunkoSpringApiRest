package org.develop.rest.categorias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaNotSave extends CategoriaException{

    public CategoriaNotSave(String categoria) {
        super("La categoria " + categoria + " ya existe en la BD");
    }
}
