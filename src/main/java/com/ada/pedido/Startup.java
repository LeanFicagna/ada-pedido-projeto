package com.ada.pedido;

import com.ada.pedido.repositories.ClienteRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class Startup {

    @Transactional
    public void criarAdmin(@Observes StartupEvent event, ClienteRepository clienteRepository) {
        System.out.println("Criando usuário admin...");
        if(clienteRepository.findByEmail("admin@ada.com").isEmpty()){
            var admin = new com.ada.pedido.repositories.ClienteEntity();
            admin.setNome("Administrador");
            admin.setEmail("admin@ada.com");
            admin.setSenha(io.quarkus.elytron.security.common.BcryptUtil.bcryptHash("admin123"));
            admin.setTipoUsuario(com.ada.pedido.repositories.TipoUsuario.ADMIN);
            clienteRepository.persist(admin);
        }


        System.out.println("Criando usuário admin...");
    }

}
