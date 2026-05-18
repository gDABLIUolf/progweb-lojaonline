package com.vesteBem.controller;

import com.vesteBem.model.Categoria;
import com.vesteBem.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Categoria criar(@RequestBody Categoria categoria) {
        return repository.save(categoria);
    }
}