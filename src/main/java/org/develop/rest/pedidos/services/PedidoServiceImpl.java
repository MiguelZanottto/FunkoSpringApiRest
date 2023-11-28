package org.develop.rest.pedidos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.develop.rest.funkos.exceptions.FunkoNotFound;
import org.develop.rest.funkos.repositories.FunkosRepository;
import org.develop.rest.pedidos.exceptions.*;
import org.develop.rest.pedidos.models.LineaPedido;
import org.develop.rest.pedidos.models.Pedido;
import org.develop.rest.pedidos.repositories.PedidoRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig( cacheNames = {"pedidos"})
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final FunkosRepository funkosRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, FunkosRepository funkosRepository) {
        this.pedidoRepository = pedidoRepository;
        this.funkosRepository = funkosRepository;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        log.info("Obteniendo todos los pedidos paginados y ordenados con {}", pageable);
        return pedidoRepository.findAll(pageable);
    }

    @Override
    @Cacheable(key = "#idPedido")
    public Pedido findById(ObjectId idPedido) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return pedidoRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario, pageable);
    }


    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido: {}", pedido);

        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);

        pedidoToSave.setFechaCreacion(LocalDateTime.now());
        pedidoToSave.setFechaActualizacion(LocalDateTime.now());

        return pedidoRepository.save(pedidoToSave);
    }

    Pedido reserveStockPedidos(Pedido pedido){
        log.info("Reservando stock del pedido: {}", pedido);

        if(pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()){
            throw new PedidoNotItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkosRepository.findById(lineaPedido.getIdFunko()).get();

            funko.setCantidad(funko.getCantidad() - lineaPedido.getCantidad());

            if(funko.getCantidad() < 0){
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }

            funkosRepository.save(funko);

            lineaPedido.setTotal(lineaPedido.getCantidad() * lineaPedido.getPrecioFunko());
        });

        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioFunko())
                .reduce(0.0, Double::sum);

        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);

        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);

        return pedido;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#idPedido")
    public void deleteById(ObjectId idPedido) {
        log.info("Borrando pedido: " + idPedido);
        var pedidoDelete = this.findById(idPedido);
        returnStockPedidos(pedidoDelete);
        pedidoRepository.deleteById(idPedido);
    }


    Pedido returnStockPedidos(Pedido pedido){
        log.info("Retornando stock del pedido: {}", pedido);
        if(pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                var funko = funkosRepository.findById(lineaPedido.getIdFunko()).get();
                funko.setCantidad(funko.getCantidad() + lineaPedido.getCantidad());
                funkosRepository.save(funko);
            });
        }
        return pedido;
    }

    @Override
    @Transactional
    @CachePut(key = "#idPedido")
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);
        var pedidoFound = this.findById(idPedido);
        returnStockPedidos(pedidoFound);
        checkPedido(pedido);
        var pedidoToSave = reserveStockPedidos(pedido);
        pedidoToSave.setId(idPedido);
        pedidoToSave.setFechaActualizacion(LocalDateTime.now());
        return pedidoRepository.save(pedidoToSave);
    }


    void checkPedido(Pedido pedido){
        log.info("Comprobando pedido: {}", pedido);
        if(pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()){
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            if(lineaPedido.getIdFunko() == null){
                throw new FunkoBadRequest();
            }

            var funko = funkosRepository.findById(lineaPedido.getIdFunko()).orElseThrow(() -> new FunkoNotFound(lineaPedido.getIdFunko()));

            if(funko.getCantidad() < lineaPedido.getCantidad() && lineaPedido.getCantidad() > 0){
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }

            if(!funko.getPrecio().equals(lineaPedido.getPrecioFunko())){
                throw new FunkoBadPrice(lineaPedido.getIdFunko());
            }
        });

    }
}