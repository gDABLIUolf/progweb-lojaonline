package com.vesteBem.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoResponseDTO(
        Long carrinhoId,
        Long usuarioId,
        List<ItemResponseDTO> itens,
        BigDecimal subtotal
) {
    public record ItemResponseDTO(
            Long itemId,
            Long produtoId,
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal precoTotal
    ) {}
}