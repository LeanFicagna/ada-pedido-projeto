package com.ada.pedido.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        String email,
        @NotBlank
        String senha
) {
}
