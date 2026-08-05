package com.ada.pedido.repositories;

import com.ada.pedido.repositories.entities.ProdutoEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepositoryBase<ProdutoEntity, Long> {

	public PanacheQuery<ProdutoEntity> findByDescricaoLike(String descricao) {
		return find("LOWER(descricao) like ?1", "%" + descricao.toLowerCase() + "%");
	}

}
