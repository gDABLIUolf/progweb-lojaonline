package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PedidoStatusUpdateRequestDTO(
        @Schema(description = "Novo status do pedido", example = "PAGO")
        @NotBlank(message = "O status não pode estar em branco")
        String status,

        @Schema(description = "Método de pagamento utilizado", example = "Cartão de Crédito")
        String metodoPagamento,

        @Schema(description = "Valor total pago final", example = "239.42")
        java.math.BigDecimal valorTotalPago
) {}