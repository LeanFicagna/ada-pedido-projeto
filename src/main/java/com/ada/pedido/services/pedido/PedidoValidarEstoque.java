package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Priority(50)
@ApplicationScoped
public class PedidoValidarEstoque implements  ProcessarPedido {

    private final ProdutoRepository produtoRepository;

    @Inject
    public PedidoValidarEstoque(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void processar(PedidoEntity pedidoEntity) {
        for (ItemPedidoEntity item : pedidoEntity.getItems()) {
            validarEstoque(pedidoEntity, item);
        }
    }

    private void validarEstoque(PedidoEntity pedido, ItemPedidoEntity itemPedido){

        var optional = produtoRepository.findByIdOptional(itemPedido.getProduto().getId());
        if (optional.isEmpty()){
            throw new RuntimeException("Produto não encontrado");
        }

        var produto = optional.get();
        if (produto.getEstoque() < itemPedido.getQuantidade()){
            pedido.setStatus(StatusPedido.NAO_PROCESSADO);
            pedido.setMensagemStatus("Estoque insuficiente para o produto "+ produto.getDescricao());
        }

    }
}
