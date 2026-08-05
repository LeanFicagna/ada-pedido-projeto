package com.ada.pedido.resources.exceptions;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenericExceptionMapperTest {

    @Test
    void toResponse_deveMappearExcecaoGenerica() {
        Exception exception = new Exception("Erro genérico");
        GenericExceptionMapper mapper = new GenericExceptionMapper();

        Response response = mapper.toResponse(exception);

        assertEquals(500, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void toResponse_deveMappearBusinessException() {
        BusinessException exception = new BusinessException("Erro de negócio");
        GenericExceptionMapper mapper = new GenericExceptionMapper();

        Response response = mapper.toResponse(exception);

        // BusinessException também cai no toResponse genérico
        assertEquals(400, response.getStatus());
    }
}
