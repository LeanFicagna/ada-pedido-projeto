package com.ada.pedido.resources.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequest(
        @NotNull
        Long produtoId,
        @NotNull
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
