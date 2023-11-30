package org.develop.pedidos.services;

import org.bson.types.ObjectId;
import org.develop.rest.funkos.models.Funko;
import org.develop.rest.funkos.repositories.FunkosRepository;
import org.develop.rest.pedidos.exceptions.PedidoNotFound;
import org.develop.rest.pedidos.models.LineaPedido;
import org.develop.rest.pedidos.models.Pedido;
import org.develop.rest.pedidos.repositories.PedidoRepository;
import org.develop.rest.pedidos.services.PedidoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidosServiceImplTest {
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private FunkosRepository funkosRepository;
    @InjectMocks
    private PedidoServiceImpl pedidosService;

    @Test
    void findAll(){
        List<Pedido> pedidoList = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidoList);
        Pageable pageable = PageRequest.of(0, 10);

        when(pedidoRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Pedido> actualPage = pedidoRepository.findAll(pageable);

        assertAll(
                () -> assertEquals(expectedPage, actualPage),
                () -> assertEquals(expectedPage.getContent(), actualPage.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements())
        );

        verify(pedidoRepository, times(1)).findAll(pageable);
    }

    @Test
    void findById(){
        ObjectId idPedido = new ObjectId();
        Pedido expectedPedido = new Pedido();

        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(expectedPedido));

        Pedido actualPedido = pedidosService.findById(idPedido);

        assertEquals(expectedPedido, actualPedido);

        verify(pedidoRepository).findById(idPedido);
    }

    @Test
    void findById_NotFound(){
        ObjectId idPedido = new ObjectId();

        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));

        verify(pedidoRepository).findById(idPedido);
    }

    @Test
    void save(){
        Funko funko = Funko.builder()
                .id(1L)
                .cantidad(20)
                .precio(19.99)
                .build();

        Pedido pedido = new Pedido();

        LineaPedido lineaPedido = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(10)
                .precioFunko(19.99)
                .build();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = new Pedido();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoToSave);
        when(funkosRepository.findById(anyLong())).thenReturn(Optional.of(funko));

        Pedido actualPedido = pedidosService.save(pedido);

        assertAll(
                () -> assertEquals(pedidoToSave, actualPedido),
                () -> assertEquals(pedidoToSave.getLineasPedido(), actualPedido.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), actualPedido.getLineasPedido().size())
        );

        verify(pedidoRepository).save(any(Pedido.class));
        verify(funkosRepository, times(2)).findById(anyLong());
    }

    @Test
    void delete(){
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelete = new Pedido();
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelete));

        pedidosService.deleteById(idPedido);

        verify(pedidoRepository).findById(idPedido);
        verify(pedidoRepository).deleteById(idPedido);
    }

    @Test
    void update() {
        Funko funko = Funko.builder()
                .id(1L)
                .cantidad(20)
                .precio(19.99)
                .build();

        LineaPedido lineaPedido = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(10)
                .precioFunko(19.99)
                .build();

        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToUpdate = new Pedido();
        pedidoToUpdate.setLineasPedido(List.of(lineaPedido));

        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToUpdate));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoToUpdate);
        when(funkosRepository.findById(anyLong())).thenReturn(Optional.of(funko));

        Pedido actualPedido = pedidosService.update(idPedido, pedido);

        assertAll(
                () -> assertEquals(pedidoToUpdate, actualPedido),
                () -> assertEquals(pedidoToUpdate.getLineasPedido(), actualPedido.getLineasPedido()),
                () -> assertEquals(pedidoToUpdate.getLineasPedido().size(), actualPedido.getLineasPedido().size())
        );

        verify(pedidoRepository).findById(idPedido);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(funkosRepository, times(3)).findById(anyLong());
    }

}
