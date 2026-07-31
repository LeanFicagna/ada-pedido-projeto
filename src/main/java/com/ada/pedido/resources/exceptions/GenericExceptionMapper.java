package com.ada.pedido.resources.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {

        //ConstraintViolationException
        if(exception instanceof org.hibernate.exception.ConstraintViolationException) {
            System.err.println("Erro de ConstraintViolationException: " + exception.getMessage());

            var errorResponse = new ErrorResponse(
                    "Registro ja existe ",
                    java.time.LocalDateTime.now()
            );

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        } else if(exception instanceof BusinessException){

            var errorResponse = new ErrorResponse(
                    "Erro de negocio: " + exception.getMessage(),
                    java.time.LocalDateTime.now()
            );

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();

        } else {
            var errorResponse = new ErrorResponse(
                    "Erro interno do servidor: " + exception.getMessage(),
                    java.time.LocalDateTime.now()
            );

            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .build();
        }
    }
}
