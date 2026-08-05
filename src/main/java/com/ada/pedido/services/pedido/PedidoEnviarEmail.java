package com.ada.pedido.services.pedido;

import com.ada.pedido.repositories.entities.PedidoEntity;
import com.ada.pedido.repositories.entities.StatusPedido;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.format.DateTimeFormatter;

@Priority(10)
@ApplicationScoped
public class PedidoEnviarEmail implements  ProcessarPedido {

    @Override
    public void processar(PedidoEntity pedidoEntity) {

        try {

            if (StatusPedido.NAO_PROCESSADO.equals(pedidoEntity.getStatus())) {
                return;
            }

            var mensagem = criarMensagem(pedidoEntity);
            var assunto = "Pedido " + pedidoEntity.getId() + " - " + pedidoEntity.getStatus();

            System.out.println("Enviando email para o cliente: " + pedidoEntity.getCliente().getEmail() + " com assunto: " + assunto);
            System.out.println(mensagem);

        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }

    }

    private String criarMensagem(PedidoEntity pedidoEntity){

        var cliente = pedidoEntity.getCliente();
        var dataPedido = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(pedidoEntity.getDatePedido());

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Olá ").append(cliente.getNome()).append(",\n\n");
        mensagem.append("Seu pedido foi realizado com sucesso!\n\n");
        mensagem.append("Detalhes do pedido:\n");
        mensagem.append("Data do pedido: ").append(dataPedido).append("\n");
        mensagem.append("Status do pedido: ").append(pedidoEntity.getStatus()).append("\n");
        mensagem.append("Mensagem de status: ").append(pedidoEntity.getMensagemStatus()).append("\n\n");

        mensagem.append("Itens do pedido:\n");
        for (var item : pedidoEntity.getItems()) {
            mensagem.append("- ")
                    .append(item.getQuantidade())
                    .append("x ")
                    .append(item.getProduto().getDescricao())
                    .append(" - R$")
                    .append(item.getPreco()).append("\n");
        }
        mensagem.append("\n");
        mensagem.append("Obrigado por comprar conosco!\n");
        mensagem.append("Atenciosamente,\n");
        mensagem.append("Equipe Ada");

        return mensagem.toString();

    }
}
