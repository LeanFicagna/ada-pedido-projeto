package com.ada.pedido.resources;

import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.resources.dto.ClienteRequest;
import com.ada.pedido.resources.dto.ClienteResponse;
import com.ada.pedido.resources.exceptions.BusinessException;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Authenticated
@Path("/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    private final ClienteRepository clienteRepository;
    private final SecurityIdentity securityIdentity;

    public ClienteResource(ClienteRepository clienteRepository, SecurityIdentity securityIdentity) {
        this.clienteRepository = clienteRepository;
        this.securityIdentity = securityIdentity;
    }

    @POST
    @Path("/criar")
    @Transactional
    @PermitAll
    public Response criarCliente(@Valid ClienteRequest cliente) {

        if (cliente.nome().length() < 5){
            throw new BusinessException("Nome do cliente não pode ter menos de 5 caracteres");
        }

        var entity = cliente.criarEntity();
        clienteRepository.persist(entity);

        return Response
                .status(Response.Status.CREATED)
                .entity(ClienteResponse.fromEntity(entity))
                .build();

    }

    @GET
    @Path("/listar")
    @RolesAllowed({"ADMIN"})
    public Response listarClientes() {


        var panacheQuery = clienteRepository.findAll();
        var listaClientes = panacheQuery.list();

//        var listaClientesDTO = listaClientes.stream()
//                .map(ClienteDTO::fromEntity)
//                .toList();

        var listaClientesDTO = new ArrayList<>();
        for (ClienteEntity cliente : listaClientes) {
            listaClientesDTO.add(ClienteResponse.fromEntity(cliente));
        }

        return Response
                .status(Response.Status.OK)
                .entity(listaClientesDTO)
                .build();
    }

    @GET
    @Path("/buscar/{id}")
    @RolesAllowed({"ADMIN"})
    public Response buscarClientePorId(@PathParam("id") Long id) {

        var clienteEntity = clienteRepository.findById(id);

        if (clienteEntity == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Cliente não encontrado\"}")
                    .build();
        }

        return Response
                .status(Response.Status.OK)
                .entity(ClienteResponse.fromEntity(clienteEntity))
                .build();

    }

    @DELETE
    @Path("/deletar/{id}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response deletarCliente(@PathParam("id") Long id) {

        clienteRepository.deleteById(id);

        //var cliente = clienteRepository.findById(id);

//        if (!deletado) {
//            return Response
//                    .status(Response.Status.NOT_FOUND)
//                    .entity("{\"message\": \"Cliente não encontrado\"}")
//                    .build();
//        }

        //clienteRepository.delete(cliente);

        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @PUT
    @Path("/atualizar/{id}")
    @Transactional
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response atualizarCliente(@PathParam("id") Long id, @Valid ClienteRequest clienteAtualizado) {

        validaClienteHaAlterar(id);

        var clienteEntity = clienteRepository.findById(id);

        if (clienteEntity == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Cliente não encontrado\"}")
                    .build();
        }

        clienteEntity.setNome(clienteAtualizado.nome());
        clienteEntity.setEmail(clienteAtualizado.email());

        clienteRepository.persist(clienteEntity);

        return Response
                .status(Response.Status.OK)
                .entity(ClienteResponse.fromEntity(clienteEntity))
                .build();
    }

    @PATCH
    @Path("/atualizar-parcialmente/{id}")
    @Transactional
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response atualizarClienteParcialmente(@PathParam("id") Long id, ClienteRequest clienteAtualizado) {

        validaClienteHaAlterar(id);

        var clienteEntity = clienteRepository.findById(id);
        if (clienteEntity == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Cliente não encontrado\"}")
                    .build();
        }

        if (clienteAtualizado.nome() != null) {
            clienteEntity.setNome(clienteAtualizado.nome());
        }
        if (clienteAtualizado.email() != null) {
            clienteEntity.setEmail(clienteAtualizado.email());
        }

        clienteRepository.persist(clienteEntity);

        return Response
                .status(Response.Status.OK)
                .entity(ClienteResponse.fromEntity(clienteEntity))
                .build();
    }

    @GET
    @Path("/buscar-por-email/{email}")
    @RolesAllowed({"ADMIN"})
    public Response buscarClientePorEmail(@PathParam("email") String email) {
        var cliente = clienteRepository.findByEmail(email);

        if (cliente.isEmpty()) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Cliente não encontrado\"}")
                    .build();
        }

        var clienteDTO = ClienteResponse.fromEntity(cliente.get());

        return Response
                .status(Response.Status.OK)
                .entity(clienteDTO)
                .build();
    }

    @GET
    @Path("/buscar-por-nome/{nome}")
    @RolesAllowed({"ADMIN"})
    public Response buscarClientePorNome(@PathParam("nome") String nome) {
        var panacheQuery = clienteRepository.findByNameLike(nome);
        var listaClientes = panacheQuery.list();

        var listaClientesDTO = listaClientes.stream()
                .map(ClienteResponse::fromEntity)
                .toList();

        return Response
                .status(Response.Status.OK)
                .entity(listaClientesDTO)
                .build();
    }

    private void validaClienteHaAlterar(Long id) {
        if (id == null) {
            throw new BusinessException("Id do cliente não pode ser nulo");
        }

        String userEmail = securityIdentity.getPrincipal().getName();
        var optional = clienteRepository.findByEmail(userEmail);
        if (optional.isEmpty()) {
            throw new BusinessException("Usuário logado não encontrado");
        }

        var userLogado = optional.get();
        if (!id.equals(userLogado.getId()) && userLogado.getTipoUsuario().name().equals("CLIENTE")) {
            throw new BusinessException("Você não pode atualizar outro cliente");
        }
    }

}
