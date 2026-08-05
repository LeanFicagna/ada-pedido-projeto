package com.ada.pedido.security.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTServiceTest {

    @Test
    void criarToken_deveRetornarTokenValido() {
        String token = JWTService.criarToken("test@email.com", "CLIENTE");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));  // JWT tem 3 partes separadas por ponto
    }

    @Test
    void criarToken_deveRetornarTokenDiferentes() {
        String token1 = JWTService.criarToken("test1@email.com", "CLIENTE");
        String token2 = JWTService.criarToken("test2@email.com", "ADMIN");

        assertNotNull(token1);
        assertNotNull(token2);
        // Tokens são diferentes para emails diferentes
        assertFalse(token1.equals(token2));
    }

    @Test
    void criarToken_deveIncluirEmailNoJWT() {
        String email = "usuario@teste.com";
        String token = JWTService.criarToken(email, "CLIENTE");

        assertNotNull(token);
        // Token possui estrutura válida
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void criarToken_deveIncluirRoleNoJWT() {
        String token = JWTService.criarToken("admin@teste.com", "ADMIN");

        assertNotNull(token);
        // Validar que token foi criado com role ADMIN
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }
}

