package com.ada.pedido.resources.dto;

import com.ada.pedido.repositories.ClienteEntity;
import com.ada.pedido.repositories.ProdutoEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long id,
        @NotBlank(message = "O descrição é obrigatório") // Valida nulos, vazios e espaços em branco
        String descricao,
        @NotBlank(message = "O preco é obrigatório") // Valida nulos, vazios e espaços em branco
        BigDecimal preco,
        int estoque
) {

    public ProdutoEntity criarEntity() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setDescricao(this.descricao);
        produtoEntity.setPreco(this.preco);
        produtoEntity.setEstoque(this.estoque);
        return produtoEntity;
    }

    public static ProdutoDTO fromEntity(ProdutoEntity produtoEntity) {
        return new ProdutoDTO(produtoEntity.getId(), produtoEntity.getDescricao(), produtoEntity.getPreco(), produtoEntity.getEstoque());
    }
}
