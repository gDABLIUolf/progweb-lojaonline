package com.vesteBem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vesteBem.dto.CategoriaRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o ID: " + id));
    }

    public Categoria criar(CategoriaRequestDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome!");
        }
        return repository.save(new Categoria(dto.nome()));
    }

    public Categoria atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarPorId(id);
        categoria.setNome(dto.nome());
        return repository.save(categoria);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}