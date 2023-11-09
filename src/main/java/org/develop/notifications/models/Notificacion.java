package org.develop.notifications.models;

public record Notificacion<T> (
        String entity,
        Tipo type,
        T data,
        String fechaCreacion) {

    public enum Tipo{
        CREATE, UPDATE, DELETE;
    }

}
