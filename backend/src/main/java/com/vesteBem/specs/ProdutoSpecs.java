package com.vesteBem.specs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class ProdutoSpecs {

    public static Specification<Produto> filtrarDinamico(String nome, List<Long> categoriasIds, Boolean apenasDescontos, Boolean destaqueCarrossel) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (nome != null && !nome.trim().isEmpty()) {
                predicados.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                ));
            }

            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                for (Long catId : categoriasIds) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Produto> subqueryRoot = subquery.from(Produto.class);
                    Join<Produto, Categoria> subqueryJoin = subqueryRoot.join("categorias");
                    subquery.select(subqueryRoot.get("id"))
                            .where(criteriaBuilder.equal(subqueryJoin.get("id"), catId));
                    predicados.add(root.get("id").in(subquery));
                }
            }

            if (apenasDescontos != null && apenasDescontos) {
                predicados.add(criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("desconto")),
                        criteriaBuilder.greaterThan(root.get("desconto"), 0)
                ));
            }

            if (destaqueCarrossel != null) {
                predicados.add(criteriaBuilder.equal(root.get("destaqueCarrossel"), destaqueCarrossel));
            }

            return criteriaBuilder.and(predicados.toArray(new Predicate[0]));
        };
    }
}