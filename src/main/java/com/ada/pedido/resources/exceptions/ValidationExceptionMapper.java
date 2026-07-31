package com.ada.pedido.resources.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        var message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.joining("| "));

        var errorResponse = new ErrorResponse(
                "Erro de validacao: " + message,
                java.time.LocalDateTime.now()
        );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();

    }
}
