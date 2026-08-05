package com.ada.pedido.security;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.TipoUsuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginResourceTest {

    private ClienteRepository clienteRepository;
    private LoginResource loginResource;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        loginResource = new LoginResource(clienteRepository);
    }

    @Test
    void login_deveRetornarTokenQuandoCredenciaisValidas() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setEmail("admin@ada.com");
        cliente.setNome("Admin");
        cliente.setSenha(BcryptUtil.bcryptHash("admin123"));
        cliente.setTipoUsuario(TipoUsuario.ADMIN);

        when(clienteRepository.findByEmail("admin@ada.com")).thenReturn(Optional.of(cliente));

        LoginRequest loginRequest = new LoginRequest("admin@ada.com", "admin123");
        var response = loginResource.login(loginRequest);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        verify(clienteRepository).findByEmail("admin@ada.com");
    }

    @Test
    void login_deveRetornar401QuandoClienteNaoExiste() {
        when(clienteRepository.findByEmail("inexistente@ada.com")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest("inexistente@ada.com", "123456");
        var response = loginResource.login(loginRequest);

        assertEquals(401, response.getStatus());
        verify(clienteRepository).findByEmail("inexistente@ada.com");
    }

    @Test
    void login_deveRetornar401QuandoSenhaInvalida() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(1L);
        cliente.setEmail("admin@ada.com");
        cliente.setSenha(BcryptUtil.bcryptHash("admin123"));
        cliente.setTipoUsuario(TipoUsuario.ADMIN);

        when(clienteRepository.findByEmail("admin@ada.com")).thenReturn(Optional.of(cliente));

        LoginRequest loginRequest = new LoginRequest("admin@ada.com", "senhaerrada");
        var response = loginResource.login(loginRequest);

        assertEquals(401, response.getStatus());
        verify(clienteRepository).findByEmail("admin@ada.com");
    }

    @Test
    void login_deveRetornarTokenParaClienteComRoleCLIENTE() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(2L);
        cliente.setEmail("cliente@ada.com");
        cliente.setNome("Cliente");
        cliente.setSenha(BcryptUtil.bcryptHash("cliente123"));
        cliente.setTipoUsuario(TipoUsuario.CLIENTE);

        when(clienteRepository.findByEmail("cliente@ada.com")).thenReturn(Optional.of(cliente));

        LoginRequest loginRequest = new LoginRequest("cliente@ada.com", "cliente123");
        var response = loginResource.login(loginRequest);

        assertEquals(200, response.getStatus());
        LoginResponse loginResponse = (LoginResponse) response.getEntity();
        assertNotNull(loginResponse.token());
    }
}
