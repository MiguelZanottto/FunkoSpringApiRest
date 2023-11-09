package org.develop.notifications.mappers;

import org.develop.funkos.models.Funko;
import org.develop.notifications.dto.FunkoNotificationDto;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationDto toFunkoNotificationDto(Funko funko) {
        return new FunkoNotificationDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getImagen(),
                funko.getFechaCreacion().toString(),
                funko.getFechaActualizacion().toString(),
                funko.getIsActivo(),
                funko.getCategoria().getNombre()
        );
    }
}
