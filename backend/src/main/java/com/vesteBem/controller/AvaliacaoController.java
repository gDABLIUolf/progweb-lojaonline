package com.vesteBem.controller;

import com.vesteBem.dto.AvaliacaoRequestDTO;
import com.vesteBem.dto.AvaliacaoResponseDTO;
import com.vesteBem.model.Avaliacao;
import com.vesteBem.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "Endpoints para gerenciamento de avaliações de produtos")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping("/{usuarioId}/produto/{produtoId}")
    @Operation(summary = "Avaliar Produto", description = "Registra a nota e o comentário de um usuário para um produto.")
    public ResponseEntity<?> avaliarProduto(
            @PathVariable Long usuarioId,
            @PathVariable Long produtoId,
            @RequestBody @Valid AvaliacaoRequestDTO dto) {
        try {
            Avaliacao avaliacaoSalva = avaliacaoService.avaliarProduto(usuarioId, produtoId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AvaliacaoResponseDTO(avaliacaoSalva));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar Avaliações", description = "Retorna todas as avaliações de um produto específico.")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoesDoProduto(@PathVariable Long produtoId) {
        List<AvaliacaoResponseDTO> lista = avaliacaoService.listarAvaliacoesPorProduto(produtoId)
                .stream().map(AvaliacaoResponseDTO::new).toList();
        return ResponseEntity.ok(lista);
    }
}