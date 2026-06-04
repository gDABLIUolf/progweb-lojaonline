package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(

        @Schema(description = "Nome da categoria", example = "Camisetas")
        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome
) {}