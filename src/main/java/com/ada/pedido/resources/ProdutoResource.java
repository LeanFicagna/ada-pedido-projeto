package com.ada.pedido.resources;

import com.ada.pedido.repositories.ProdutoEntity;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.resources.dto.ProdutoDTO;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Authenticated
@Path("/produtos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    private final ProdutoRepository produtoRepository;

    public ProdutoResource(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @POST
    @Path("/criar")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response criarProduto(@Valid ProdutoDTO produto) {
        var entity = produto.criarEntity();
        produtoRepository.persist(entity);

        return Response
           .status(Response.Status.CREATED)
           .entity(ProdutoDTO.fromEntity(entity))
           .build();
    }

    @GET
    @Path("/listar")
    @PermitAll
    public Response listarProdutos() {
        var listaProdutos = produtoRepository.findAll().list();

        var listaProdutosDTO = new ArrayList<ProdutoDTO>();
        for (ProdutoEntity produto : listaProdutos) {
            listaProdutosDTO.add(ProdutoDTO.fromEntity(produto));
        }

        return Response
           .status(Response.Status.OK)
           .entity(listaProdutosDTO)
           .build();
    }

    @GET
    @Path("/buscar/{id}")
    @PermitAll
    public Response buscarProdutoPorId(@PathParam("id") Long id) {
        var produtoEntity = produtoRepository.findById(id);

        if (produtoEntity == null) {
            return Response
               .status(Response.Status.NOT_FOUND)
               .entity("{\"message\": \"Produto não encontrado\"}")
               .build();
        }

        return Response
           .status(Response.Status.OK)
           .entity(ProdutoDTO.fromEntity(produtoEntity))
           .build();
    }

    @GET
    @Path("/buscar-por-descricao/{descricao}")
    @PermitAll
    public Response buscarProdutoPorDescricao(@PathParam("descricao") String descricao) {
        var listaProdutos = produtoRepository.findByDescricaoLike(descricao).list();

        var listaProdutosDTO = listaProdutos.stream()
                .map(ProdutoDTO::fromEntity)
                .toList();

        return Response
           .status(Response.Status.OK)
           .entity(listaProdutosDTO)
           .build();
    }

    @DELETE
    @Path("/deletar/{id}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response deletarProduto(@PathParam("id") Long id) {
        produtoRepository.deleteById(id);

        return Response
           .status(Response.Status.NO_CONTENT)
           .build();
    }

    @PUT
    @Path("/atualizar/{id}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response atualizarProduto(@PathParam("id") Long id, @Valid ProdutoDTO produtoAtualizado) {
        var produtoEntity = produtoRepository.findById(id);

        if (produtoEntity == null) {
            return Response
               .status(Response.Status.NOT_FOUND)
               .entity("{\"message\": \"Produto não encontrado\"}")
               .build();
        }

        produtoEntity.setDescricao(produtoAtualizado.descricao());
        produtoEntity.setPreco(produtoAtualizado.preco());
        produtoEntity.setEstoque(produtoAtualizado.estoque());

        produtoRepository.persist(produtoEntity);

        return Response
           .status(Response.Status.OK)
           .entity(ProdutoDTO.fromEntity(produtoEntity))
           .build();
    }

    @PATCH
    @Path("/atualizar-parcialmente/{id}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response atualizarProdutoParcialmente(@PathParam("id") Long id, ProdutoDTO produtoAtualizado) {
        var produtoEntity = produtoRepository.findById(id);

        if (produtoEntity == null) {
            return Response
               .status(Response.Status.NOT_FOUND)
               .entity("{\"message\": \"Produto não encontrado\"}")
               .build();
        }

        if (produtoAtualizado.descricao() != null) {
            produtoEntity.setDescricao(produtoAtualizado.descricao());
        }
        if (produtoAtualizado.preco() != null) {
            produtoEntity.setPreco(produtoAtualizado.preco());
        }
        if (produtoAtualizado.estoque() != 0) {
            produtoEntity.setEstoque(produtoAtualizado.estoque());
        }

        produtoRepository.persist(produtoEntity);

        return Response
           .status(Response.Status.OK)
           .entity(ProdutoDTO.fromEntity(produtoEntity))
           .build();
    }
}