package org.develop.rest.pedidos.services;

import org.bson.types.ObjectId;
import org.develop.rest.pedidos.models.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoService {
    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);

    Pedido save(Pedido pedido);

    void deleteById(ObjectId idPedido);

    Pedido update(ObjectId idPedido, Pedido pedido);
}
