package org.develop.pedidos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("pedidos")
@TypeAlias("Pedido")
@EntityListeners(AuditingEntityListener.class)
public class Pedido {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @NotNull(message = "El id del cliente no puede ser nulo")
    private @Valid Cliente cliente ;
    @NotNull(message = "El pedido debe tener al menos una linea de pedido")
    @Valid
    private List< @Valid LineaPedido> lineasPedido;
    @Builder.Default
    private Integer totalItems = 0;
    @Builder.Default
    private Double total = 0.0;
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @UpdateTimestamp
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
    @Builder.Default
    private Boolean isActivo = true;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }

    public void setLineasPedido(List<LineaPedido> lineasPedido){
        this.lineasPedido = lineasPedido;
        this.totalItems = lineasPedido != null ? lineasPedido.size() : 0;
        this.total = lineasPedido != null ? lineasPedido.stream().mapToDouble(LineaPedido::getTotal).sum() : 0.0;
    }
}

