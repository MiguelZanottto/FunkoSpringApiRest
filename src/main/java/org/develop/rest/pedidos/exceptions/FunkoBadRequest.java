package org.develop.rest.pedidos.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoBadRequest extends PedidoException {
    public FunkoBadRequest() {
        super("El Funko esta nulo");
    }
}