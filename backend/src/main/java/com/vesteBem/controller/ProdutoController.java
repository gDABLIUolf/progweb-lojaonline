package com.vesteBem.controller;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.dto.ProdutoResponseDTO;
import com.vesteBem.model.Produto;
import com.vesteBem.model.ProdutoImagem;
import com.vesteBem.repository.ProdutoImagemRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.service.ProdutoService;
import com.vesteBem.specs.ProdutoSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;
    private final ProdutoImagemRepository produtoImagemRepository;

    public ProdutoController(ProdutoService produtoService, ProdutoRepository produtoRepository, ProdutoImagemRepository produtoImagemRepository) {
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
        this.produtoImagemRepository = produtoImagemRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoResponseDTO> criar(
            @RequestPart("dados") @Valid ProdutoRequestDTO dto,
            @RequestPart(value = "imagens", required = false) List<MultipartFile> imagens,
            @RequestPart(value = "imagemCarrossel", required = false) MultipartFile imagemCarrossel) throws IOException {

        ProdutoResponseDTO salvo = produtoService.cadastrar(dto, imagens, imagemCarrossel);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/{id}/imagem")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<byte[]> buscarImagem(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .filter(p -> p.getImagens() != null && !p.getImagens().isEmpty())
                .map(p -> {
                    // Pega a primeira da lista (a capa)
                    var imagem = p.getImagens().get(0);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, imagem.getTipo())
                            .body(imagem.getDados());
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/imagem-carrossel")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<byte[]> buscarImagemCarrossel(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .filter(p -> p.getImagemCarrossel() != null)
                .map(p -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, p.getTipoImagemCarrossel() != null ? p.getTipoImagemCarrossel() : "image/png")
                        .body(p.getImagemCarrossel()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @GetMapping("/{id}/galeria")
    public ResponseEntity<List<Long>> listarGaleria(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<Long> ids = produto.getImagens().stream()
                .map(ProdutoImagem::getId)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/imagem/{imagemId}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<byte[]> buscarImagemPorId(@PathVariable Long imagemId) {
        ProdutoImagem imagem = produtoImagemRepository.findById(imagemId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, imagem.getTipo())
                .body(imagem.getDados());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar produtos (Vitrine)")
    public ResponseEntity<List<ProdutoResponseDTO>> listarVitrine(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<Long> categoriasIds,
            @RequestParam(required = false) Boolean apenasDescontos,
            @RequestParam(required = false) Boolean destaqueCarrossel) {
        return ResponseEntity.ok(produtoService.listarTodos(nome, categoriasIds, apenasDescontos, destaqueCarrossel));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar um produto existente")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestPart("dados") @Valid ProdutoRequestDTO dto,
            @RequestPart(value = "imagens", required = false) List<MultipartFile> imagens,
            @RequestPart(value = "imagemCarrossel", required = false) MultipartFile imagemCarrossel) throws IOException {

        ProdutoResponseDTO atualizado = produtoService.atualizar(id, dto, imagens, imagemCarrossel);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um produto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}