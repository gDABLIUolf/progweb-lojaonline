package com.vesteBem.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        List<String> categoriasNomes // Alterado de String para List<String>
) {
}