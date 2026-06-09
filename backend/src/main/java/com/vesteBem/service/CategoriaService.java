package com.vesteBem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vesteBem.dto.CategoriaRequestDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.repository.CategoriaRepository;
import org.springframework.web.multipart.MultipartFile;

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

    public Categoria criar(CategoriaRequestDTO dto, MultipartFile file) throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());

        if (file != null && !file.isEmpty()) {
            categoria.setImagem(file.getBytes());
            categoria.setTipoImagem(file.getContentType());
        }

        return repository.save(categoria);
    }

    public Categoria atualizar(Long id, CategoriaRequestDTO dto, MultipartFile file) throws Exception {
        Categoria categoria = buscarPorId(id);
        categoria.setNome(dto.nome());

        if (file != null && !file.isEmpty()) {
            categoria.setImagem(file.getBytes());
            categoria.setTipoImagem(file.getContentType());
        }
        return repository.save(categoria);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}