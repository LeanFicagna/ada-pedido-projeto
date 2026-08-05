package com.ada.pedido.resources.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        String descricaoProduto,
        BigDecimal precoProduto,
        Integer quantidade,
        BigDecimal totalItem
) {
}
