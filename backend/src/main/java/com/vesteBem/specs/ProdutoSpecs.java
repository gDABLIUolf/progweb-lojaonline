package com.vesteBem.specs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ProdutoSpecs {

    public static Specification<Produto> filtrarDinamico(String nome, List<Long> categoriasIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (nome != null && !nome.trim().isEmpty()) {
                predicados.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                ));
            }

            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                Join<Produto, Categoria> joinCategorias = root.join("categorias");
                predicados.add(joinCategorias.get("id").in(categoriasIds));
            }

            return criteriaBuilder.and(predicados.toArray(new Predicate[0]));
        };
    }
}