package com.vesteBem.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vesteBem.dto.CategoriaRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }


    @GetMapping("/{id}/imagem")
    @Operation(summary = "Visualizar a imagem da categoria")
    public ResponseEntity<byte[]> buscarImagem(@PathVariable Long id) {
        Categoria categoria = service.buscarPorId(id);

        if (categoria.getImagem() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, categoria.getTipoImagem())
                .body(categoria.getImagem());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cadastrar uma nova categoria com imagem")
    public ResponseEntity<?> criar(
            @RequestPart("dados") @Valid CategoriaRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile file) {
        try {
            Categoria categoriaSalva = service.criar(dto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar uma categoria existente e sua imagem")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestPart("dados") @Valid CategoriaRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile file) {
        try {
            Categoria categoriaAtualizada = service.atualizar(id, dto, file);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover uma categoria")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}