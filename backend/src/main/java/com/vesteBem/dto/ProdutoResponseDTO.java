package com.vesteBem.dto;

import com.vesteBem.model.Produto;
import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque, // Adicionado aqui
        String categoriaNome
) {
    public ProdutoResponseDTO(Produto produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getDescription(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(), // Mapeado aqui
                produto.getCategoria().getNome()
        );
    }
}