package org.develop.notifications.dto;



public record FunkoNotificationDto(
        Long id,
        String nombre,
        Double precio,
        Integer cantidad,
        String imagen,
        String fechaCreacion,
        String fechaActualizacion,
        Boolean isActivo,
        String categoria)   {
}