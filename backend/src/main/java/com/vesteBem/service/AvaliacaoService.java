package com.vesteBem.service;

import com.vesteBem.dto.AvaliacaoRequestDTO;
import com.vesteBem.model.Avaliacao;
import com.vesteBem.model.Produto;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.AvaliacaoRepository;
import com.vesteBem.repository.ItemPedidoRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, ProdutoRepository produtoRepository, 
                            UsuarioRepository usuarioRepository, ItemPedidoRepository itemPedidoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public Avaliacao avaliarProduto(Long usuarioId, Long produtoId, AvaliacaoRequestDTO dto) {
        boolean comprou = itemPedidoRepository.existsByPedidoUsuarioIdAndProdutoIdAndPedidoStatusIn(
                usuarioId, produtoId, List.of("CONCLUIDO", "ENTREGUE")
        );
        if (!comprou) {
            throw new IllegalArgumentException("Você só pode avaliar produtos de pedidos concluídos!");
        }

        if (avaliacaoRepository.existsByUsuarioIdAndProdutoId(usuarioId, produtoId)) {
            throw new IllegalArgumentException("Você já avaliou este produto!");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        Avaliacao avaliacao = new Avaliacao(produto, usuario, dto.nota(), dto.comentario());
        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarAvaliacoesPorUsuario(Long usuarioId) {
        return avaliacaoRepository.findByUsuarioId(usuarioId);
    }

    public List<Avaliacao> listarAvaliacoesPorProduto(Long produtoId) {
        return avaliacaoRepository.findByProdutoId(produtoId);
    }

    public List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    public void deletar(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada com o ID: " + id));
        avaliacaoRepository.delete(avaliacao);
    }

    public Avaliacao atualizar(Long id, AvaliacaoRequestDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada com o ID: " + id));
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        return avaliacaoRepository.save(avaliacao);
    }
}