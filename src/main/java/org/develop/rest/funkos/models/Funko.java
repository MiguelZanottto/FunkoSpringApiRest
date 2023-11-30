package org.develop.rest.funkos.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.develop.rest.categorias.models.Categoria;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Builder
@Table(name = "FUNKOS")
public class Funko {
    public static final String IMAGE_DEFAULT = "https://via.placeholder.com/150";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del funko", example = "1")
    private final Long id;
    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre del funko", example = "Cristiano Ronaldo")
    private String nombre;
    @Column(name = "precio", columnDefinition = "double precision default 0.0")
    @Schema(description = "Precio del funko", example = "100.0")
    @Builder.Default
    private Double precio = 0.0;
    @Column(name = "cantidad", columnDefinition = "integer default 0")
    @Schema(description = "Cantidad del funko", example = "20")
    @Builder.Default
    private Integer cantidad = 0;
    @Column(columnDefinition = "TEXT default '" + IMAGE_DEFAULT + "'") // Por defecto una imagen
    @Schema(description = "Imagen del funko", example = "https://via.placeholder.com/150")
    private String imagen;
    @Column(updatable = false, nullable = false,  columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de creacion del funko", example = "2023-01-01T00:00:00.000Z")
    private LocalDateTime fechaCreacion;
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de actualizacion del funko", example = "2023-01-01T00:00:00.000Z")
    private LocalDateTime fechaActualizacion;
    @Column(columnDefinition = "boolean default true")
    @Schema(description = "Si el funko esta activo", example = "true")
    private final Boolean isActivo;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @Schema(description = "Categoria del funko", example = "SUPERHEROES")
    private Categoria categoria;
}
