package org.develop.pedidos.exceptions;

public abstract class PedidoException extends RuntimeException{
    public PedidoException(String message) {
        super(message);
    }
}
