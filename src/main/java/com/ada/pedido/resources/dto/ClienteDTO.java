package com.ada.pedido.resources.dto;

import com.ada.pedido.repositories.entities.ClienteEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(
        Long id,
        @NotBlank(message = "O nome é obrigatório") // Valida nulos, vazios e espaços em branco
        String nome,
        @NotBlank(message = "O email é obrigatório") // Valida nulos, vazios e espaços em branco
        @Email(message = "O email deve ser válido") // Valida se o email é válido
        String email
) {

    public ClienteEntity criarEntity() {
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNome(this.nome);
        clienteEntity.setEmail(this.email);
        return clienteEntity;
    }

    public static ClienteDTO fromEntity(ClienteEntity clienteEntity) {
        return new ClienteDTO(clienteEntity.getId(), clienteEntity.getNome(), clienteEntity.getEmail());
    }
}
