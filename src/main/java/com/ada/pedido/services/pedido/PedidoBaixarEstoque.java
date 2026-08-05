package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.PedidoRepository;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Priority(20)
@ApplicationScoped
public class PedidoBaixarEstoque implements  ProcessarPedido {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;


    @Inject
    public PedidoBaixarEstoque(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void processar(PedidoEntity pedidoEntity) {

        if (StatusPedido.NAO_PROCESSADO.equals(pedidoEntity.getStatus())){
            // SALVAR O PEDIDO NO BANCO, SEM OS ITEM.
            return;
        }

        for (ItemPedidoEntity item : pedidoEntity.getItems()) {
            atualizarEstoque(item);
        }
        pedidoEntity.setStatus(StatusPedido.PROCESSADO);
        pedidoRepository.persist(pedidoEntity);

    }

    private void atualizarEstoque(ItemPedidoEntity itemPedido){

        var optional = produtoRepository.findByIdOptional(itemPedido.getProduto().getId());
        if (optional.isPresent()) {
            var produto = optional.get();
            produto.setEstoque(produto.getEstoque() - itemPedido.getQuantidade());
            produtoRepository.persist(produto);
        }

    }
}
