package com.vesteBem.dto;

import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        List<String> categoriasNomes,
        Double mediaAvaliacoes,
        List<AvaliacaoResponseDTO> avaliacoes
) {
    public ProdutoResponseDTO(Produto produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getCategorias() != null ? 
                        produto.getCategorias().stream().map(Categoria::getNome).toList() : List.of(),
                
                // Calcula a média das notas (ex: 4.5). Se não tiver avaliação, retorna 0.0
                produto.getAvaliacoes() != null && !produto.getAvaliacoes().isEmpty() ?
                        Math.round(produto.getAvaliacoes().stream().mapToDouble(a -> a.getNota()).average().orElse(0.0) * 10.0) / 10.0 : 0.0,
                
                // Converte as avaliações para DTO
                produto.getAvaliacoes() != null ?
                        produto.getAvaliacoes().stream().map(AvaliacaoResponseDTO::new).toList() : List.of()
        );
    }
}