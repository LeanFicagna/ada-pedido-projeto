package com.ada.pedido.resources;

import com.ada.pedido.resources.dto.ItemPedidoRequest;
import com.ada.pedido.resources.dto.ItemPedidoResponse;
import com.ada.pedido.resources.dto.PedidoRequest;
import com.ada.pedido.resources.dto.PedidoResponse;
import com.ada.pedido.services.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoResourceTest {

    private PedidoService pedidoService;
    private PedidoResource pedidoResource;

    @BeforeEach
    void setUp() {
        pedidoService = mock(PedidoService.class);
        pedidoResource = new PedidoResource(pedidoService);
    }

    @Test
    void realizarPedido_deveRetornarPedidoResponse() {
        var itemRequest = new ItemPedidoRequest(1L, 2);
        var pedidoRequest = new PedidoRequest(List.of(itemRequest));

        var itemResponse = new ItemPedidoResponse(
                "Notebook",
                new BigDecimal("4599.90"),
                2,
                new BigDecimal("9199.80")
        );

        var pedidoResponse = new PedidoResponse(
                1L,
                LocalDateTime.now(),
                "Cliente",
                "PROCESSADO",
                List.of(itemResponse),
                new BigDecimal("9199.80")
        );

        when(pedidoService.criar(pedidoRequest)).thenReturn(pedidoResponse);

        var response = pedidoResource.realizarPedido(pedidoRequest);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getEntity());
        verify(pedidoService).criar(pedidoRequest);
    }

    @Test
    void listarPedidos_deveRetornarListaVaziaOuComPedidos() {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        var response = pedidoResource.listarPedidos();

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        verify(pedidoService).listarTodos();
    }

    @Test
    void listarPedidos_deveRetornarListaComPedidos() {
        var itemResponse = new ItemPedidoResponse(
                "Mouse",
                new BigDecimal("129.90"),
                1,
                new BigDecimal("129.90")
        );

        var pedidoResponse = new PedidoResponse(
                1L,
                LocalDateTime.now(),
                "Joao",
                "PROCESSADO",
                List.of(itemResponse),
                new BigDecimal("129.90")
        );

        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoResponse));

        var response = pedidoResource.listarPedidos();

        assertEquals(200, response.getStatus());
        verify(pedidoService).listarTodos();
    }

    @Test
    void realizarPedido_deveRetornarStatusProcessado() {
        var itemRequest = new ItemPedidoRequest(2L, 1);
        var pedidoRequest = new PedidoRequest(List.of(itemRequest));

        var itemResponse = new ItemPedidoResponse(
                "Mouse",
                new BigDecimal("129.90"),
                1,
                new BigDecimal("129.90")
        );

        var pedidoResponse = new PedidoResponse(
                2L,
                LocalDateTime.now(),
                "Maria",
                "PROCESSADO",
                List.of(itemResponse),
                new BigDecimal("129.90")
        );

        when(pedidoService.criar(pedidoRequest)).thenReturn(pedidoResponse);

        var response = pedidoResource.realizarPedido(pedidoRequest);

        assertEquals(201, response.getStatus());
        PedidoResponse pedido = (PedidoResponse) response.getEntity();
        assertEquals("PROCESSADO", pedido.status());
        assertEquals("Maria", pedido.cliente());
    }
}

