package com.vesteBem.service;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.dto.ProdutoResponseDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.model.ProdutoImagem;
import com.vesteBem.repository.CategoriaRepository;
import com.vesteBem.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto, List<MultipartFile> imagens) throws IOException {
        List<Categoria> categorias = categoriaRepository.findAllById(dto.categoriasIds());

        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setCategorias(categorias);

        if (imagens != null && !imagens.isEmpty()) {
            for (MultipartFile file : imagens.stream().limit(5).collect(Collectors.toList())) {
                ProdutoImagem imagem = new ProdutoImagem();
                imagem.setDados(file.getBytes());
                imagem.setTipo(file.getContentType());
                imagem.setProduto(produto);
                produto.getImagens().add(imagem);
            }
        }

        Produto produtoSalvo = produtoRepository.save(produto);
        return new ProdutoResponseDTO(produtoSalvo);
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));
        return new ProdutoResponseDTO(produto);
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto, List<MultipartFile> imagens) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));

        List<Categoria> categorias = categoriaRepository.findAllById(dto.categoriasIds());

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setCategorias(categorias);

        if (imagens != null && !imagens.isEmpty()) {
            produto.getImagens().clear();

            for (MultipartFile file : imagens.stream().limit(5).collect(Collectors.toList())) {
                ProdutoImagem imagem = new ProdutoImagem();
                imagem.setDados(file.getBytes());
                imagem.setTipo(file.getContentType());
                imagem.setProduto(produto);
                produto.getImagens().add(imagem);
            }
        }

        Produto produtoAtualizado = produtoRepository.save(produto);
        return new ProdutoResponseDTO(produtoAtualizado);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));
        produtoRepository.delete(produto);
    }
}