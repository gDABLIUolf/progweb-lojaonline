package com.vesteBem.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        List<Long> categoriasIds,
        Integer desconto,
        Boolean destaqueCarrossel
) {
}