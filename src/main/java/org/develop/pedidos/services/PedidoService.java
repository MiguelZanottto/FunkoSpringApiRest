package org.develop.pedidos.services;

import org.bson.types.ObjectId;
import org.develop.pedidos.models.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoService {
    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Pedido save(Pedido pedido);

    void deleteById(ObjectId idPedido);

    Pedido update(ObjectId idPedido, Pedido pedido);
}