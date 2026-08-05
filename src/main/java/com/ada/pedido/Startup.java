package com.ada.pedido;

import com.ada.pedido.repositories.ClienteRepository;
import com.ada.pedido.repositories.entities.ClienteEntity;
import com.ada.pedido.repositories.entities.ProdutoEntity;
import com.ada.pedido.repositories.ProdutoRepository;
import com.ada.pedido.repositories.entities.TipoUsuario;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class Startup {

    @Transactional
    public void criarAdmin(@Observes StartupEvent event, ClienteRepository clienteRepository) {
        System.out.println("Criando usuário admin...");
        if (clienteRepository.findByEmail("admin@ada.com").isEmpty()) {
            var admin = new ClienteEntity();
            admin.setNome("Administrador");
            admin.setEmail("admin@ada.com");
            admin.setSenha(io.quarkus.elytron.security.common.BcryptUtil.bcryptHash("admin123"));
            admin.setTipoUsuario(TipoUsuario.ADMIN);
            clienteRepository.persist(admin);
        }


        System.out.println("Criando usuário admin...");
    }

    @Transactional
    public void criarProduto(@Observes StartupEvent evt, ProdutoRepository repository) {
        var produtosIniciais = List.of(
                novoProduto("Notebook Pro 14", new BigDecimal("4599.90"), 8),
                novoProduto("Mouse Sem Fio", new BigDecimal("129.90"), 40),
                novoProduto("Teclado Mecânico", new BigDecimal("349.90"), 25),
                novoProduto("Monitor 27 Polegadas", new BigDecimal("1399.00"), 12),
                novoProduto("Headset Gamer", new BigDecimal("279.90"), 20)
        );

        System.out.println("Criando produtos de teste...");
        for (ProdutoEntity produto : produtosIniciais) {
            if (repository.findByDescricaoLike(produto.getDescricao()).count() == 0) {
                repository.persist(produto);
            }
        }

        System.out.println("Produtos disponíveis para teste:");
        for (ProdutoEntity produto : produtosIniciais) {
            repository.findByDescricaoLike(produto.getDescricao())
                    .firstResultOptional()
                    .ifPresent(p -> System.out.println("Produto: " + p.getId() +
                                                       " - Descrição: " + p.getDescricao() +
                                                       " - Preço: " + p.getPreco() +
                                                       " - Estoque: " + p.getEstoque()));
        }
    }

    private ProdutoEntity novoProduto(String descricao, BigDecimal preco, int estoque) {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setEstoque(estoque);
        return produto;
    }
}
