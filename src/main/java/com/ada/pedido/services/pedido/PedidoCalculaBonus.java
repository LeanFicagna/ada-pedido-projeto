package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.entities.PedidoEntity;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

@Priority(9)
public class PedidoCalculaBonus implements ProcessarPedido {
    @Override
    public void processar(PedidoEntity pedidoEntity) {

        // Lógica para calcular o bônus do pedido

        // Calcular bonus baseado no valor total
        // enviar informaçao para API de bonus

    }
}
