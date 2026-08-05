package com.ada.pedido.resources.dto;

import com.ada.pedido.repositories.entities.ClienteEntity;

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
