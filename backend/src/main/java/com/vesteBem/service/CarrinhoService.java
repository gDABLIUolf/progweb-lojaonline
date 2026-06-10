package com.vesteBem.service;

import com.vesteBem.dto.CarrinhoResponseDTO;
import com.vesteBem.dto.CarrinhoResponseDTO.ItemResponseDTO;
import com.vesteBem.dto.ItemCarrinhoRequestDTO;
import com.vesteBem.model.Carrinho;
import com.vesteBem.model.ItemCarrinho;
import com.vesteBem.model.Produto;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.CarrinhoRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItem(Long usuarioId, ItemCarrinhoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carrinhoRepository.save(new Carrinho(usuario)));

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        Optional<ItemCarrinho> itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().setQuantidade(itemExistente.get().getQuantidade() + dto.quantidade());
        } else {
            ItemCarrinho novoItem = new ItemCarrinho(carrinho, produto, dto.quantidade());
            carrinho.getItens().add(novoItem);
        }

        carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO removerItem(Long usuarioId, Long produtoId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não está no carrinho."));

        if (item.getQuantidade() > 1) {
            item.setQuantidade(item.getQuantidade() - 1);
        } else {
            carrinho.getItens().remove(item);
        }

        carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO removerItemCompleto(Long usuarioId, Long produtoId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não está no carrinho."));

        carrinho.getItens().remove(item);
        carrinhoRepository.save(carrinho);
        return converterParaDTO(carrinho);
    }

    public CarrinhoResponseDTO listarCarrinho(Long usuarioId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));
        return converterParaDTO(carrinho);
    }

    private CarrinhoResponseDTO converterParaDTO(Carrinho carrinho) {
        BigDecimal subtotal = BigDecimal.ZERO;

        List<ItemResponseDTO> itensDTO = carrinho.getItens().stream().map(item -> {
            BigDecimal precoTotalItem = item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            return new ItemResponseDTO(
                    item.getId(),
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getProduto().getPreco(),
                    precoTotalItem
            );
        }).collect(Collectors.toList());

        for (ItemResponseDTO item : itensDTO) {
            subtotal = subtotal.add(item.precoTotal());
        }

        return new CarrinhoResponseDTO(carrinho.getId(), carrinho.getUsuario().getId(), itensDTO, subtotal);
    }
}