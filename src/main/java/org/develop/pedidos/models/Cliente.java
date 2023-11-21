package org.develop.pedidos.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;


public record Cliente(
        @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
        String nombreCompleto,
        @Email(message = "El email debe ser valido")
        String email,
        @NotBlank(message = "El telefono no puede estar vacio")
        @Pattern(regexp = "^[679][0-9]{8}$", message = "El telefono debe comenzar con 9, 6 o 7 y tener  9 numeros")
        String telefono,
        @NotNull(message = "La direccion no puede ser nula")
        @Valid Direccion direccion
) {
}
