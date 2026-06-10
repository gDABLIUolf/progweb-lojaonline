package com.vesteBem.dto;

import com.vesteBem.model.Avaliacao;
import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        Long id,
        Double nota, // Trocado para Double
        String comentario,
        LocalDateTime dataAvaliacao,
        String nomeUsuario
) {
    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        this(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataAvaliacao(),
                avaliacao.getUsuario().getEmail().split("@")[0] 
        );
    }
}