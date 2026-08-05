package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoCalculaBonusTest {

    private final PedidoCalculaBonus pedidoCalculaBonus = new PedidoCalculaBonus();

    @Test
    void processar_deveExecutarSemErroQuandoPedidoProcessado() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setDescricao("Produto");
        produto.setPreco(new BigDecimal("100"));

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPreco(new BigDecimal("100"));

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Cliente Teste");

        PedidoEntity pedido = new PedidoEntity();
        pedido.setStatus(StatusPedido.PROCESSADO);
        pedido.setCliente(cliente);
        pedido.setItems(List.of(item));

        // Não deve lançar exceção
        pedidoCalculaBonus.processar(pedido);

        assertEquals(StatusPedido.PROCESSADO, pedido.getStatus());
    }

    @Test
    void processar_deveIgnorarQuandoNaoProcessado() {
        PedidoEntity pedido = new PedidoEntity();
        pedido.setStatus(StatusPedido.NAO_PROCESSADO);
        pedido.setItems(List.of());

        // Não deve lançar exceção
        pedidoCalculaBonus.processar(pedido);

        assertEquals(StatusPedido.NAO_PROCESSADO, pedido.getStatus());
    }
}

