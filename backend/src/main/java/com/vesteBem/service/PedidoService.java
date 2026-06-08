package com.vesteBem.service;

import com.vesteBem.dto.ItemPedidoRequestDTO;
import com.vesteBem.dto.ItemPedidoResponseDTO;
import com.vesteBem.dto.PedidoRequestDTO;
import com.vesteBem.dto.PedidoResponseDTO;
import com.vesteBem.model.ItemPedido;
import com.vesteBem.model.Pedido;
import com.vesteBem.model.Produto;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.ItemPedidoRepository;
import com.vesteBem.repository.PedidoRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Pedido processarCheckout(PedidoRequestDTO pedidoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoRequestDTO.usuarioId())
                .orElseThrow(()-> new IllegalArgumentException("Usuário não encontrado!"));

        Pedido pedido = new Pedido(usuario);
        pedido = pedidoRepository.save(pedido);

        for (ItemPedidoRequestDTO itemDTO : pedidoRequestDTO.itens()){
            Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(()-> new IllegalArgumentException("Produto ID " + itemDTO.produtoId() + " não encontrado!"));

            if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome() +
                        ". Disponível em estoque: " + produto.getQuantidadeEstoque());
            }

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDTO.quantidade());
            produtoRepository.save(produto);

            ItemPedido itemPedido = new ItemPedido(
                    pedido,
                    produto,
                    itemDTO.quantidade(),
                    produto.getPreco()
            );
            itemPedidoRepository.save(itemPedido);
        }
        return pedido;
    }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarHistoricoPorUsuario(Long usuarioId) {
        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuarioId);

        return pedidos.stream().map(pedido -> {
            List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());

            List<ItemPedidoResponseDTO> itensDto = itens.stream()
                    .map(ItemPedidoResponseDTO::new)
                    .toList();

            return new PedidoResponseDTO(pedido, itensDto);
        }).toList();
    }

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido ID " + pedidoId + " não encontrado!"));

        String statusFormatado = novoStatus.trim().toUpperCase();
        pedido.setStatus(statusFormatado);
        return pedidoRepository.save(pedido);
    }
}
