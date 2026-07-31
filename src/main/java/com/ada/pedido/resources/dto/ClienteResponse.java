package com.ada.pedido.resources.dto;

import com.ada.pedido.repositories.ClienteEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        String typoCliente
) {


    public static ClienteResponse fromEntity(ClienteEntity clienteEntity) {
        return new ClienteResponse(
                clienteEntity.getId(),
                clienteEntity.getNome(),
                clienteEntity.getEmail(),
                clienteEntity.getTipoUsuario().name()
        );
    }
}
