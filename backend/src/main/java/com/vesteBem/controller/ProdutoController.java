package com.vesteBem.controller;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.dto.ProdutoResponseDTO;
import com.vesteBem.model.Produto;
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

    public ProdutoController(ProdutoService produtoService, ProdutoRepository produtoRepository) {
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cadastrar produto vinculado a múltiplas categorias com imagem")
    public ResponseEntity<ProdutoResponseDTO> criar(
            @RequestPart("dados") @Valid ProdutoRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        ProdutoResponseDTO salvo = produtoService.cadastrar(dto, imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    @Operation(summary = "Listar produtos (Vitrine)")
    public ResponseEntity<List<ProdutoResponseDTO>> listarVitrine() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/{id}/imagem")
    @Operation(summary = "Buscar a imagem do produto")
    public ResponseEntity<byte[]> buscarImagem(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .filter(produto -> produto.getImagem() != null)
                .map(produto -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, produto.getTipoImagem())
                        .body(produto.getImagem()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar um produto existente com nova imagem")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestPart("dados") @Valid ProdutoRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        ProdutoResponseDTO atualizado = produtoService.atualizar(id, dto, imagem);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um produto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/busca")
    @Operation(summary = "Busca dinâmica por nome e múltiplas categorias")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarDinamicamente(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<Long> categorias) {

        Specification<Produto> regraDeBusca = ProdutoSpecs.filtrarDinamico(nome, categorias);
        List<Produto> resultadosEntity = produtoRepository.findAll(regraDeBusca);

        List<ProdutoResponseDTO> resultadosDTO = resultadosEntity.stream()
                .map(ProdutoResponseDTO::new) // Usa o construtor inteligente que resolve tudo
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultadosDTO);
    }
}