package com.vesteBem.controller;

import com.vesteBem.dto.PedidoRequestDTO;
import com.vesteBem.dto.PedidoResponseDTO;
import com.vesteBem.dto.PedidoStatusUpdateRequestDTO;
import com.vesteBem.model.Pedido;
import com.vesteBem.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name="Pedidos", description = "Endpoints para finalização e consulta de compras")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Finalizar compra", description = "Transforma os itens do carrinho em um pedido oficial.")
    public ResponseEntity<?> realizarCheckout(@RequestBody @Valid PedidoRequestDTO pedidoRequestDTO) {
        try {
            Pedido pedidoSalvo = pedidoService.processarCheckout(pedidoRequestDTO);
            return ResponseEntity.ok().body(pedidoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Histórico de pedidos do usuário", description = "Retorna todas as compras realizadas por um cliente específico.")
    public ResponseEntity<List<PedidoResponseDTO>> listarHistorico(@PathVariable Long usuarioId) {
        List<PedidoResponseDTO> historico = pedidoService.buscarHistoricoPorUsuario(usuarioId);
        return ResponseEntity.ok(historico);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Altera o status de um pedido (ex: PENDENTE para PAGO ou CANCELADO).")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid PedidoStatusUpdateRequestDTO dto) {
        try {
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, dto.status());
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/carrinho/{usuarioId}")
    @Operation(summary = "Finalizar carrinho", description = "Transforma o carrinho do usuário em um pedido oficial e esvazia o carrinho.")
    public ResponseEntity<?> finalizarCarrinho(@PathVariable Long usuarioId) {
        try {
            Pedido pedidoSalvo = pedidoService.finalizarCarrinho(usuarioId);
            return ResponseEntity.ok().body(pedidoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}