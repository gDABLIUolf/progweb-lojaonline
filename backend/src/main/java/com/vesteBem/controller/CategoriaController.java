package com.vesteBem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vesteBem.dto.CategoriaRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.repository.CategoriaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova categoria")
    public ResponseEntity<Categoria> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria(dto.nome());
        Categoria salva = repository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }
}