package com.vesteBem.controller;

import com.vesteBem.dto.CategoriaRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias de produtos")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova categoria", description = "Cadastra uma nova categoria no catálogo.")
    public ResponseEntity<Categoria> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
        Categoria novaCategoria = new Categoria(dto.nome());

        Categoria categoriaSalva = repository.save(novaCategoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma categoria", description = "Altera o nome de uma categoria existente.")
    public ResponseEntity<Categoria> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaRequestDTO dto) {

        return repository.findById(id).map(categoria -> {
            categoria.setNome(dto.nome());
            Categoria atualizada = repository.save(categoria);
            return ResponseEntity.ok(atualizada);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover uma categoria", description = "Exclui uma categoria do sistema.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}