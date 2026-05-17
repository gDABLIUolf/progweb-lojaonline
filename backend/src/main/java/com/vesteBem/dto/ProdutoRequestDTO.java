package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @Schema(example = "Camiseta Streetwear Oversized")
        @NotBlank(message = "O nome do produto é obrigatório")
        String nome,

        @Schema(example = "Camiseta 100% algodão fio 30.1 penteado.")
        String descricao,

        @Schema(example = "89.90")
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @Schema(description = "ID da categoria vinculada", example = "1")
        @NotNull(message = "O ID da categoria é obrigatório")
        Long categoriaId
) {}