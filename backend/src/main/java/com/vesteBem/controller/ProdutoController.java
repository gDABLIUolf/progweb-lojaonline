package com.vesteBem.controller;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.dto.ProdutoResponseDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto vinculado a uma categoria")
    public ResponseEntity<Produto> criar(@RequestBody @Valid ProdutoRequestDTO dto) {
        Categoria categoria = new Categoria(dto.categoriaId());

        Produto produto = new Produto(dto.nome(), dto.descricao(), dto.preco(), dto.quantidadeEstoque(), categoria);
        Produto salvo = produtoRepository.save(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    @Operation(summary = "Listar produtos (Vitrine)", description = "Retorna o catálogo de produtos com paginação. Padrão: 10 itens por página.")
    public ResponseEntity<List<ProdutoResponseDTO>> listarVitrine() {
        List<Produto> produtos = produtoRepository.findAll();

        List<ProdutoResponseDTO> vitrine = produtos.stream()
                .map(ProdutoResponseDTO::new)
                .toList();

        return ResponseEntity.ok(vitrine);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico para a página de detalhes.")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ProdutoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto existente", description = "Altera os dados de um produto com base no ID.")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProdutoRequestDTO dto) {

        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(dto.nome());
            produto.setDescricao(dto.descricao());
            produto.setPreco(dto.preco());
            produto.setQuantidadeEstoque(dto.quantidadeEstoque());

            Categoria categoria = new Categoria(dto.categoriaId());
            produto.setCategoria(categoria);

            Produto atualizado = produtoRepository.save(produto);

            return ResponseEntity.ok(new ProdutoResponseDTO(atualizado));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um produto", description = "Exclui permanentemente um produto do catálogo.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        produtoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}