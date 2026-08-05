package com.ada.pedido.resources.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        LocalDateTime dataHora,
        String cliente,
        String status,
        List<ItemPedidoResponse> items,
        BigDecimal totalPedido
) {
}
