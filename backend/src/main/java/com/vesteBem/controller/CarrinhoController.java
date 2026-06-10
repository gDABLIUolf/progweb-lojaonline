package com.vesteBem.controller;

import com.vesteBem.dto.ItemCarrinhoRequestDTO;
import com.vesteBem.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinho", description = "Endpoints para gerenciar o carrinho de compras")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping("/{usuarioId}/adicionar")
    @Operation(summary = "Adicionar item", description = "Adiciona um produto ao carrinho do usuário.")
    public ResponseEntity<Object> adicionarItem(@PathVariable Long usuarioId, @RequestBody @Valid ItemCarrinhoRequestDTO dto) {
        try {
            return ResponseEntity.ok(carrinhoService.adicionarItem(usuarioId, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}/remover/{produtoId}")
    @Operation(summary = "Remover item", description = "Remove um produto específico do carrinho do usuário.")
    public ResponseEntity<Object> removerItem(@PathVariable Long usuarioId, @PathVariable Long produtoId) {
        try {
            return ResponseEntity.ok(carrinhoService.removerItem(usuarioId, produtoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}/remover-tudo/{produtoId}")
    @Operation(summary = "Remover item completo", description = "Remove todas as unidades de um produto do carrinho de uma vez.")
    public ResponseEntity<Object> removerItemCompleto(@PathVariable Long usuarioId, @PathVariable Long produtoId) {
        try {
            return ResponseEntity.ok(carrinhoService.removerItemCompleto(usuarioId, produtoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Listar carrinho", description = "Retorna todos os itens do carrinho e o subtotal calculado.")
    public ResponseEntity<Object> listarCarrinho(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(carrinhoService.listarCarrinho(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}