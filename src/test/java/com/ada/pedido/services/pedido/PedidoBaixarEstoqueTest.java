package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.PedidoRepository;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoBaixarEstoqueTest {

    private ProdutoRepository produtoRepository;
    private PedidoRepository pedidoRepository;
    private PedidoBaixarEstoque pedidoBaixarEstoque;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        pedidoBaixarEstoque = new PedidoBaixarEstoque(produtoRepository, pedidoRepository);
    }

    @Test
    void processar_deveIgnorarQuandoPedidoNaoProcessado() {
        PedidoEntity pedido = new PedidoEntity();
        pedido.setStatus(StatusPedido.NAO_PROCESSADO);
        pedido.setItems(List.of());

        pedidoBaixarEstoque.processar(pedido);

        verify(produtoRepository, never()).persist(any(ProdutoEntity.class));
        verify(pedidoRepository, never()).persist(any(PedidoEntity.class));
    }

    @Test
    void processar_deveBaixarEstoqueEPersistirPedido() {
        ProdutoEntity produtoNoPedido = new ProdutoEntity();
        produtoNoPedido.setId(7L);

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setProduto(produtoNoPedido);
        item.setQuantidade(3);

        PedidoEntity pedido = new PedidoEntity();
        pedido.setStatus(StatusPedido.NOVO);
        pedido.setItems(List.of(item));

        ProdutoEntity produtoNoBanco = new ProdutoEntity();
        produtoNoBanco.setId(7L);
        produtoNoBanco.setEstoque(10);
        when(produtoRepository.findByIdOptional(7L)).thenReturn(Optional.of(produtoNoBanco));

        pedidoBaixarEstoque.processar(pedido);

        assertEquals(StatusPedido.PROCESSADO, pedido.getStatus());

        ArgumentCaptor<ProdutoEntity> produtoCaptor = ArgumentCaptor.forClass(ProdutoEntity.class);
        verify(produtoRepository).persist(produtoCaptor.capture());
        assertEquals(7, produtoCaptor.getValue().getEstoque());

        verify(pedidoRepository).persist(pedido);
    }
}

