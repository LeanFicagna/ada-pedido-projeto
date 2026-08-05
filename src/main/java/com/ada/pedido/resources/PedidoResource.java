package com.ada.pedido.resources;


import com.ada.pedido.resources.dto.PedidoRequest;
import com.ada.pedido.services.PedidoService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Authenticated
@Path("/pedidos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PedidoResource {

    private final PedidoService pedidoService;

    public PedidoResource(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @POST
    @RolesAllowed({"CLIENTE","ADMIN"})
    public Response realizarPedido(@Valid PedidoRequest pedido) {

        var pedidoResponse =  pedidoService.criar(pedido);

        return Response.status(201)
                .entity(pedidoResponse)
                .build();
    }

    @GET
    @RolesAllowed({"CLIENTE","ADMIN"})
    public Response listarPedidos() {
        var pedidos = pedidoService.listarTodos();
        return Response.ok(pedidos).build();
    }
}
