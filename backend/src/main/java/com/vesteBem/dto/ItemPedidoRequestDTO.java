package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequestDTO(
        @Schema(description = "ID do produto", example = "1")
        @NotNull(message = "O ID do produto é obrigatório")
        Long produtoId,

        @Schema(description = "Quantidade comprada", example = "2")
        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {}