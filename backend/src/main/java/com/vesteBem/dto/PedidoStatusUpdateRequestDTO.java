package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PedidoStatusUpdateRequestDTO(
        @Schema(description = "Novo status do pedido", example = "PAGO")
        @NotBlank(message = "O status não pode estar em branco")
        String status
) {}