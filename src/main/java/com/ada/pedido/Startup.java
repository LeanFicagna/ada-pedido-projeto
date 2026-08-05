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
        System.out.println("Criando produto...");
        if (repository.findByDescricaoLikeIgnoreCase("Produto para teste").isEmpty()) {
            ProdutoEntity produto = new ProdutoEntity();
            produto.setDescricao("Produto para teste");
            produto.setPreco(BigDecimal.TEN);
            produto.setEstoque(10);
            repository.persist(produto);
        }
        System.out.println("Produto Criado!");
        var produto = repository.findByDescricaoLikeIgnoreCase("Produto para teste").get(0);
        System.out.println("Produto: " + produto.getId() +
                           " - Descrição: " + produto.getDescricao() +
                           " - Preço: " + produto.getPreco() +
                           " - Estoque: " + produto.getEstoque());
    }
}
