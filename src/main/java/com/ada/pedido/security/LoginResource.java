package com.ada.pedido.security;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.security.jwt.JWTService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginResource {

    private final ClienteRepository clienteRepository;

    public LoginResource(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response login(LoginRequest loginRequest){

        var optionalCliente = clienteRepository.findByEmail(loginRequest.email());
        if (optionalCliente.isEmpty()){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        var cliente = optionalCliente.get();
        if (!BcryptUtil.matches(loginRequest.senha(), cliente.getSenha())){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        String token = JWTService.criarToken(cliente.getEmail(), cliente.getTipoUsuario().name());

        return Response.ok(new LoginResponse(token))
                .build();

    }
}
