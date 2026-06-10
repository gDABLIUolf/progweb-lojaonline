package com.vesteBem.dto;

import com.vesteBem.model.Avaliacao;
import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        Long id,
        Double nota,
        String comentario,
        LocalDateTime dataAvaliacao,
        String nomeUsuario,
        Long produtoId,
        String produtoNome
) {
    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        this(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataAvaliacao(),
                avaliacao.getUsuario().getNome().split(" ")[0], // Use first name if available, fallback email prefix was split
                avaliacao.getProduto().getId(),
                avaliacao.getProduto().getNome()
        );
    }
}