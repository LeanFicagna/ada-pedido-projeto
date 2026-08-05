package com.ada.pedido.resources;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.TipoUsuario;
import com.ada.pedido.resources.dto.ClienteRequest;
import com.ada.pedido.resources.exceptions.BusinessException;
import io.quarkus.security.identity.SecurityIdentity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteResourceTest {

    private ClienteRepository clienteRepository;
    private SecurityIdentity securityIdentity;
    private ClienteResource clienteResource;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        securityIdentity = mock(SecurityIdentity.class);
        clienteResource = new ClienteResource(clienteRepository, securityIdentity);
    }

    @Test
    void criarCliente_deveRetornarCreatedDe201() {
        ClienteRequest clienteRequest = new ClienteRequest(
                "João Silva",
                "joao@teste.com",
                "123456"
        );

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setNome("João Silva");
        clienteEntity.setEmail("joao@teste.com");
        clienteEntity.setTipoUsuario(TipoUsuario.CLIENTE);

        var response = clienteResource.criarCliente(clienteRequest);

        assertEquals(201, response.getStatus());
        verify(clienteRepository).persist(any(ClienteEntity.class));
    }

    @Test
    void criarCliente_deveLancarErroQuandoNomeMuitoCurto() {
        ClienteRequest clienteRequest = new ClienteRequest(
                "João",
                "joao@teste.com",
                "123456"
        );

        assertThrows(BusinessException.class, () -> clienteResource.criarCliente(clienteRequest));
    }

    @Test
    void buscarClientePorEmail_deveRetornarCliente() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setNome("Maria");
        cliente.setEmail("maria@teste.com");
        cliente.setTipoUsuario(TipoUsuario.CLIENTE);

        when(clienteRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(cliente));

        var response = clienteResource.buscarClientePorEmail("maria@teste.com");

        assertEquals(200, response.getStatus());
    }

    @Test
    void buscarClientePorEmail_deveRetornar404QuandoNaoExiste() {
        when(clienteRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        var response = clienteResource.buscarClientePorEmail("inexistente@teste.com");

        assertEquals(404, response.getStatus());
    }
}
