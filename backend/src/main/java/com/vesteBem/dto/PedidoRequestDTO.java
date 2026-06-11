package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequestDTO(
        @Schema(description = "ID do usuário que está comprando", example = "1")
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @Schema(description = "Lista de itens no carrinho")
        @NotEmpty(message = "O pedido deve conter pelo menos um item")
        @Valid
        List<ItemPedidoRequestDTO> itens
) {}