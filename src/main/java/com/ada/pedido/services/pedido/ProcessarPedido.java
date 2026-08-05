package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.entities.PedidoEntity;

public interface ProcessarPedido {

    void processar(PedidoEntity pedidoEntity);

}
