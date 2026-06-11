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

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar Avaliações do Usuário", description = "Retorna todas as avaliações feitas por um usuário específico.")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoesDoUsuario(@PathVariable Long usuarioId) {
        List<AvaliacaoResponseDTO> lista = avaliacaoService.listarAvaliacoesPorUsuario(usuarioId)
                .stream().map(AvaliacaoResponseDTO::new).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar Avaliações", description = "Retorna todas as avaliações de um produto específico.")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoesDoProduto(@PathVariable Long produtoId) {
        List<AvaliacaoResponseDTO> lista = avaliacaoService.listarAvaliacoesPorProduto(produtoId)
                .stream().map(AvaliacaoResponseDTO::new).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping
    @Operation(summary = "Listar todas as avaliações")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarTodas() {
        List<AvaliacaoResponseDTO> lista = avaliacaoService.listarTodas()
                .stream().map(AvaliacaoResponseDTO::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma avaliação")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AvaliacaoRequestDTO dto) {
        try {
            Avaliacao avaliacaoAtualizada = avaliacaoService.atualizar(id, dto);
            return ResponseEntity.ok(new AvaliacaoResponseDTO(avaliacaoAtualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma avaliação")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            avaliacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}