package com.vesteBem.dto;

import com.vesteBem.model.ItemPedido;
import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal totalItem
) {
    public ItemPedidoResponseDTO(ItemPedido item) {
        this(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
        );
    }
}