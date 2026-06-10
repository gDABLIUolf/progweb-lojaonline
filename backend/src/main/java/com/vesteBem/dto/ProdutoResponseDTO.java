package com.vesteBem.dto;

import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.model.ProdutoImagem;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        List<String> categoriasNomes,
        List<Long> imagensIds, // NOVO: IDs para buscar as fotos individualmente
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

                // Mapeamento de Categorias
                produto.getCategorias() != null ?
                        produto.getCategorias().stream().map(Categoria::getNome).toList() : List.of(),

                // Mapeamento de IDs de Imagens
                produto.getImagens() != null ?
                        produto.getImagens().stream().map(ProdutoImagem::getId).toList() : List.of(),

                // Média de Avaliações
                produto.getAvaliacoes() != null && !produto.getAvaliacoes().isEmpty() ?
                        Math.round(produto.getAvaliacoes().stream().mapToDouble(a -> a.getNota()).average().orElse(0.0) * 10.0) / 10.0 : 0.0,

                // Avaliações
                produto.getAvaliacoes() != null ?
                        produto.getAvaliacoes().stream().map(AvaliacaoResponseDTO::new).toList() : List.of()
        );
    }
}