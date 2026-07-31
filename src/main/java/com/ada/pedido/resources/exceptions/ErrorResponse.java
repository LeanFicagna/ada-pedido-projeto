package com.ada.pedido.resources.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensagem,
        LocalDateTime ocorreuEm
) {
}
