package com.ada.pedido.services;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.repositories.PedidoRepository;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.ItemPedidoEntity;
import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import com.ada.pedido.resources.dto.ItemPedidoRequest;
import com.ada.pedido.resources.dto.ItemPedidoResponse;
import com.ada.pedido.resources.dto.PedidoRequest;
import com.ada.pedido.resources.dto.PedidoResponse;
import com.ada.pedido.services.pedido.ProcessarPedido;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PedidoService {

    private final SecurityIdentity securityIdentity;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    private final Instance<ProcessarPedido> listProcessarPedidoInstance;
    private final PedidoRepository pedidoRepository;


    public PedidoService(SecurityIdentity securityIdentity,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository,
                         Instance<ProcessarPedido> listProcessarPedidoInstance, PedidoRepository pedidoRepository) {
        this.securityIdentity = securityIdentity;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.listProcessarPedidoInstance = listProcessarPedidoInstance;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest pedidoRequest) {

        String clienteEmail = securityIdentity.getPrincipal().getName();
        var cliente = clienteRepository
                .findByEmail(clienteEmail)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        var pedidoEntity = new PedidoEntity();
        pedidoEntity.setDatePedido(LocalDateTime.now());
        pedidoEntity.setCliente(cliente);
        pedidoEntity.setStatus(StatusPedido.NOVO);
        pedidoEntity.setMensagemStatus("");

        var itemPedidoEntityList = pedidoRequest.items().stream()
                .map(itemPedidoRequest -> construirItemPedido(itemPedidoRequest, pedidoEntity))
                .toList();
        pedidoEntity.setItems(itemPedidoEntityList);

        for (ProcessarPedido pedido : listProcessarPedidoInstance) {
            pedido.processar(pedidoEntity);
        }

        // processamento de regras para o pedido
        // 1 - validar estoque
        // 2 - baixar estoque
        // 5 - enviar email de confirmação

        // 4 - verficar desconto
        // 6 - solicitar transporte

        var itemsResponse = new ArrayList<ItemPedidoResponse>();
        var totalPedido = BigDecimal.ZERO;

        for (ItemPedidoEntity item : pedidoEntity.getItems()) {
            var itemResponse = new ItemPedidoResponse(
                    item.getProduto().getDescricao(),
                    item.getPreco(),
                    item.getQuantidade(),
                    item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()))
            );
            itemsResponse.add(itemResponse);
            totalPedido = totalPedido.add(itemResponse.totalItem());
        }

        PedidoResponse pedidoResponse = new PedidoResponse(
                pedidoEntity.getId(),
                pedidoEntity.getDatePedido(),
                pedidoEntity.getCliente().getNome(),
                pedidoEntity.getStatus().name(),
                itemsResponse,
                totalPedido
        );

        return pedidoResponse;
    }

    public List<PedidoResponse> listarTodos(){

        var list = pedidoRepository.listAll();
        var listResponse = new ArrayList<PedidoResponse>();
        for (PedidoEntity pedido : list) {
            var itemsResponse = new ArrayList<ItemPedidoResponse>();
            var totalPedido = BigDecimal.ZERO;
            for (ItemPedidoEntity item : pedido.getItems()) {
                var itemResponse = new ItemPedidoResponse(
                        item.getProduto().getDescricao(),
                        item.getPreco(),
                        item.getQuantidade(),
                        item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()))
                );
                itemsResponse.add(itemResponse);
                totalPedido = totalPedido.add(itemResponse.totalItem());
            }
            PedidoResponse pedidoResponse = new PedidoResponse(
                    pedido.getId(),
                    pedido.getDatePedido(),
                    pedido.getCliente().getNome(),
                    pedido.getStatus().name(),
                    itemsResponse,
                    totalPedido
            );
            listResponse.add(pedidoResponse);
        }
        return listResponse;

    }

    private ItemPedidoEntity construirItemPedido(ItemPedidoRequest itemPedidoRequest, PedidoEntity pedidoEntity){

        var produto = produtoRepository.findByIdOptional(itemPedidoRequest.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ItemPedidoEntity itemPedidoEntity = new ItemPedidoEntity();
        itemPedidoEntity.setPedido(pedidoEntity);
        itemPedidoEntity.setProduto(produto);
        itemPedidoEntity.setQuantidade(itemPedidoRequest.quantidade());
        itemPedidoEntity.setPreco(produto.getPreco());

        return  itemPedidoEntity;
    }


}
