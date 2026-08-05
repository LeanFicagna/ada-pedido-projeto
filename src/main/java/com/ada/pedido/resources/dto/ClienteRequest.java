package com.ada.pedido.resources.dto;

import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.TipoUsuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "O nome é obrigatório") // Valida nulos, vazios e espaços em branco
        String nome,
        @NotBlank(message = "O email é obrigatório") // Valida nulos, vazios e espaços em branco
        @Email(message = "O email deve ser válido") // Valida se o email é válido
        String email,
        @NotBlank(message = "A senha é obrigatória") // Valida nulos, vazios e espaços em branco
        @Size(min = 6, max = 15, message = "A senha deve ter no mínimo 6 caracteres") // Valida tamanho mínimo
        String senha
) {

    public ClienteEntity criarEntity() {
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setTipoUsuario(TipoUsuario.CLIENTE); // Define o tipo de usuário como CLIENTE
        clienteEntity.setNome(this.nome);
        clienteEntity.setEmail(this.email);
        clienteEntity.setSenha(BcryptUtil.bcryptHash(this.senha)); // Criptografa a senha
        return clienteEntity;
    }

}
