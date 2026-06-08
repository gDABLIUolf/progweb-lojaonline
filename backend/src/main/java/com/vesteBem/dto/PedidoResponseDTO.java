package com.vesteBem.dto;

import com.vesteBem.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        LocalDateTime dataCriacao,
        String status,
        List<ItemPedidoResponseDTO> itens,
        BigDecimal totalPedido
) {
    public PedidoResponseDTO(Pedido pedido, List<ItemPedidoResponseDTO> itens) {
        this(
                pedido.getId(),
                pedido.getDataCriacao(),
                pedido.getStatus(),
                itens,
                itens.stream()
                        .map(ItemPedidoResponseDTO::totalItem)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }
}