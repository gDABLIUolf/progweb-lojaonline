package com.vesteBem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequestDTO(
        @NotNull(message = "A nota é obrigatória")
        @DecimalMin(value = "0.5", message = "A nota mínima é 0.5")
        @DecimalMax(value = "5.0", message = "A nota máxima é 5.0")
        Double nota,

        @Size(max = 500, message = "O comentário pode ter no máximo 500 caracteres")
        String comentario
) {
}