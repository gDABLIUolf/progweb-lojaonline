package com.vesteBem.service;

import com.vesteBem.dto.ProdutoRequestDTO;
import com.vesteBem.dto.ProdutoResponseDTO;
import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.repository.CategoriaRepository;
import com.vesteBem.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Importante para a imagem

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

    // Adicionado o parâmetro MultipartFile para a imagem
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto, MultipartFile imagem) throws IOException {

        // 1. Busca TODAS as categorias baseadas na lista de IDs que vier do Front-end
        List<Categoria> categorias = categoriaRepository.findAllById(dto.categoriasIds());

        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque()); // Faltava este campo!
        produto.setCategorias(categorias); // Agora usamos setCategorias (no plural)

        // 2. Processa a imagem se ela tiver sido enviada
        if (imagem != null && !imagem.isEmpty()) {
            produto.setImagem(imagem.getBytes());
            produto.setTipoImagem(imagem.getContentType());
        }

        Produto produtoSalvo = produtoRepository.save(produto);
        return converterParaResponseDTO(produtoSalvo);
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));
        return converterParaResponseDTO(produto);
    }

    // Adicionado o parâmetro MultipartFile para a imagem também na atualização
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto, MultipartFile imagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));

        List<Categoria> categorias = categoriaRepository.findAllById(dto.categoriasIds());

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque()); // Adicionado aqui também
        produto.setCategorias(categorias);

        // Só atualiza a imagem se uma nova for enviada (mantém a antiga caso contrário)
        if (imagem != null && !imagem.isEmpty()) {
            produto.setImagem(imagem.getBytes());
            produto.setTipoImagem(imagem.getContentType());
        }

        Produto produtoAtualizado = produtoRepository.save(produto);
        return converterParaResponseDTO(produtoAtualizado);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + id));
        produtoRepository.delete(produto);
    }

    private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        // Pega a lista de entidades Categoria e extrai apenas o nome de cada uma
        List<String> nomesCategorias = produto.getCategorias().stream()
                .map(Categoria::getNome)
                .collect(Collectors.toList());

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                nomesCategorias // Retorna a lista de nomes [ "Básicos", "Moda Inverno" ]
        );
    }
}