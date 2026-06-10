package com.vesteBem.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        List<Long> categoriasIds, // Alterado de Long para List<Long>
        Integer desconto,
        Boolean destaqueCarrossel
) {
}