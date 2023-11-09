package org.develop.funkos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.develop.categorias.models.Categoria;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "FUNKOS")
public class Funko {
    public static final String IMAGE_DEFAULT = "https://via.placeholder.com/150";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "precio", columnDefinition = "double default 0.0")
    private Double precio = 0.0;
    @Column(name = "cantidad", columnDefinition = "integer default 0")
    private Integer cantidad = 0;
    @Column(columnDefinition = "TEXT default '" + IMAGE_DEFAULT + "'") // Por defecto una imagen
    private String imagen;
    @Column(updatable = false, nullable = false,  columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion;
    @Column(columnDefinition = "boolean default true")
    private final Boolean isActivo;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
