package com.ada.pedido.resources;

import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProdutoResourceTest {

    private ProdutoRepository produtoRepository;
    private ProdutoResource produtoResource;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        produtoResource = new ProdutoResource(produtoRepository);
    }

    @Test
    void buscarProdutoPorId_deveRetornarProduto() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setDescricao("Notebook");
        produto.setPreco(new BigDecimal("4599.90"));
        produto.setEstoque(5);

        when(produtoRepository.findById(1L)).thenReturn(produto);

        var response = produtoResource.buscarProdutoPorId(1L);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        verify(produtoRepository).findById(1L);
    }

    @Test
    void buscarProdutoPorId_deveRetornar404QuandoNaoEncontrar() {
        when(produtoRepository.findById(99L)).thenReturn(null);

        var response = produtoResource.buscarProdutoPorId(99L);

        assertEquals(404, response.getStatus());
        verify(produtoRepository).findById(99L);
    }

    @Test
    void listarProdutos_deveRetornarListaTodosOsProdutos() {
        @SuppressWarnings("unchecked")
        PanacheQuery<ProdutoEntity> mockQuery = mock(PanacheQuery.class);
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setDescricao("Notebook");
        produto.setPreco(new BigDecimal("4599.90"));

        when(produtoRepository.findAll()).thenReturn(mockQuery);
        when(mockQuery.list()).thenReturn(List.of(produto));

        var response = produtoResource.listarProdutos();

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        verify(produtoRepository).findAll();
    }

    @Test
    void buscarProdutoPorDescricao_deveRetornarProdutosCorrespondentes() {
        @SuppressWarnings("unchecked")
        PanacheQuery<ProdutoEntity> mockQuery = mock(PanacheQuery.class);
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(2L);
        produto.setDescricao("Mouse");
        produto.setPreco(new BigDecimal("129.90"));

        when(produtoRepository.findByDescricaoLike("Mouse")).thenReturn(mockQuery);
        when(mockQuery.list()).thenReturn(List.of(produto));

        var response = produtoResource.buscarProdutoPorDescricao("Mouse");

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void listarProdutos_deveRetornarListaVaziaSemProdutos() {
        @SuppressWarnings("unchecked")
        PanacheQuery<ProdutoEntity> mockQuery = mock(PanacheQuery.class);

        when(produtoRepository.findAll()).thenReturn(mockQuery);
        when(mockQuery.list()).thenReturn(List.of());

        var response = produtoResource.listarProdutos();

        assertEquals(200, response.getStatus());
        verify(produtoRepository).findAll();
    }
}

