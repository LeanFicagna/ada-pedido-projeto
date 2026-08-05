package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PedidoValidarEstoqueTest {

    private ProdutoRepository produtoRepository;
    private PedidoValidarEstoque pedidoValidarEstoque;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        pedidoValidarEstoque = new PedidoValidarEstoque(produtoRepository);
    }

    @Test
    void processar_deveMarcarPedidoComoNaoProcessadoQuandoEstoqueInsuficiente() {
        ProdutoEntity produtoNoPedido = new ProdutoEntity();
        produtoNoPedido.setId(1L);

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setProduto(produtoNoPedido);
        item.setQuantidade(5);

        PedidoEntity pedido = new PedidoEntity();
        pedido.setStatus(StatusPedido.NOVO);
        pedido.setItems(List.of(item));

        ProdutoEntity produtoNoBanco = new ProdutoEntity();
        produtoNoBanco.setId(1L);
        produtoNoBanco.setDescricao("Notebook");
        produtoNoBanco.setEstoque(2);
        when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produtoNoBanco));

        pedidoValidarEstoque.processar(pedido);

        assertEquals(StatusPedido.NAO_PROCESSADO, pedido.getStatus());
        assertEquals("Estoque insuficiente para o produto Notebook", pedido.getMensagemStatus());
    }

    @Test
    void processar_deveLancarErroQuandoProdutoNaoExiste() {
        ProdutoEntity produtoNoPedido = new ProdutoEntity();
        produtoNoPedido.setId(10L);

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setProduto(produtoNoPedido);
        item.setQuantidade(1);

        PedidoEntity pedido = new PedidoEntity();
        pedido.setItems(List.of(item));

        when(produtoRepository.findByIdOptional(10L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> pedidoValidarEstoque.processar(pedido));
        assertEquals("Produto não encontrado", exception.getMessage());
    }
}

