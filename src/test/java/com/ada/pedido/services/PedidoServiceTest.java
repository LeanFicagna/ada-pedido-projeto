package com.ada.pedido.services;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.repositories.PedidoRepository;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import com.ada.pedido.resources.dto.ItemPedidoRequest;
import com.ada.pedido.resources.dto.PedidoRequest;
import com.ada.pedido.resources.dto.PedidoResponse;
import com.ada.pedido.services.pedido.ProcessarPedido;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private SecurityIdentity securityIdentity;
    private ClienteRepository clienteRepository;
    private ProdutoRepository produtoRepository;
    private Instance<ProcessarPedido> processadores;
    private PedidoRepository pedidoRepository;
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        securityIdentity = mock(SecurityIdentity.class);
        clienteRepository = mock(ClienteRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        processadores = mock(Instance.class);
        pedidoRepository = mock(PedidoRepository.class);
        pedidoService = new PedidoService(
                securityIdentity,
                clienteRepository,
                produtoRepository,
                processadores,
                pedidoRepository
        );
    }

    @Test
    void criar_deveMontarPedidoECalcularTotal() {
        Principal principal = () -> "cliente@teste.com";
        when(securityIdentity.getPrincipal()).thenReturn(principal);

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        when(clienteRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(cliente));

        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(10L);
        produto.setDescricao("Teclado");
        produto.setPreco(BigDecimal.TEN);
        produto.setEstoque(20);
        when(produtoRepository.findByIdOptional(10L)).thenReturn(Optional.of(produto));

        ProcessarPedido processador1 = mock(ProcessarPedido.class);
        ProcessarPedido processador2 = mock(ProcessarPedido.class);
        when(processadores.iterator()).thenReturn(List.of(processador1, processador2).iterator());

        PedidoRequest pedidoRequest = new PedidoRequest(List.of(new ItemPedidoRequest(10L, 3)));

        PedidoResponse response = pedidoService.criar(pedidoRequest);

        assertNotNull(response.dataHora());
        assertEquals("Cliente Teste", response.cliente());
        assertEquals("NOVO", response.status());
        assertEquals(1, response.items().size());
        assertEquals(new BigDecimal("30"), response.totalPedido());

        verify(processador1, times(1)).processar(any(PedidoEntity.class));
        verify(processador2, times(1)).processar(any(PedidoEntity.class));
    }

    @Test
    void criar_deveLancarErroQuandoClienteNaoExiste() {
        Principal principal = () -> "inexistente@teste.com";
        when(securityIdentity.getPrincipal()).thenReturn(principal);
        when(clienteRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        PedidoRequest pedidoRequest = new PedidoRequest(List.of(new ItemPedidoRequest(99L, 1)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> pedidoService.criar(pedidoRequest));
        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    void listarTodos_deveConverterPedidosParaResponse() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setDescricao("Mouse");
        produto.setPreco(new BigDecimal("7.00"));

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPreco(new BigDecimal("7.00"));

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Maria");

        PedidoEntity pedido = new PedidoEntity();
        pedido.setId(1L);
        pedido.setDatePedido(LocalDateTime.of(2026, 8, 5, 10, 0));
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.PROCESSADO);
        pedido.setItems(List.of(item));

        when(pedidoRepository.listAll()).thenReturn(List.of(pedido));

        List<PedidoResponse> resposta = pedidoService.listarTodos();

        assertEquals(1, resposta.size());
        assertEquals("Maria", resposta.get(0).cliente());
        assertEquals("PROCESSADO", resposta.get(0).status());
        assertEquals(new BigDecimal("14.00"), resposta.get(0).totalPedido());
        assertEquals("Mouse", resposta.get(0).items().get(0).descricaoProduto());
    }
}

