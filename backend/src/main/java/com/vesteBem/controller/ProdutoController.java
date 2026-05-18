package com.vesteBem.controller;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Produto produto = new Produto(dto.nome(), dto.descricao(), dto.preco(), categoria);
        Produto salvo = produtoRepository.save(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}