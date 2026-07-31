package com.ada.pedido.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<ClienteEntity, Long> {

    public Optional<ClienteEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public PanacheQuery<ClienteEntity> findByNameLike(String name) {
        return find("lower(nome) like ?1", "%" + name.toLowerCase() + "%");
    }

}
