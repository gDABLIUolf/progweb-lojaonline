package com.vesteBem.service;

import com.vesteBem.dto.ItemPedidoRequestDTO;
import com.vesteBem.dto.ItemPedidoResponseDTO;
import com.vesteBem.dto.PedidoRequestDTO;
import com.vesteBem.dto.PedidoResponseDTO;
import com.vesteBem.model.ItemPedido;
import com.vesteBem.model.Pedido;
import com.vesteBem.model.Produto;
import com.vesteBem.model.Usuario;
import com.vesteBem.model.Carrinho;
import com.vesteBem.repository.ItemPedidoRepository;
import com.vesteBem.repository.PedidoRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import com.vesteBem.repository.CarrinhoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final CarrinhoRepository carrinhoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository,
                         CarrinhoRepository carrinhoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.carrinhoRepository = carrinhoRepository;
    }

    @Transactional
    public Pedido processarCheckout(PedidoRequestDTO pedidoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoRequestDTO.usuarioId())
                .orElseThrow(()-> new IllegalArgumentException("Usuário não encontrado!"));

        // 1. Validar estoque antes de formalizar o pedido
        for (ItemPedidoRequestDTO itemDTO : pedidoRequestDTO.itens()){
            Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(()-> new IllegalArgumentException("Produto ID " + itemDTO.produtoId() + " não encontrado!"));

            if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                throw new IllegalArgumentException("Não há estoque suficiente do produto: " + produto.getNome());
            }
        }

        // 2. Criar pedido com status PENDENTE (aberto)
        Pedido pedido = new Pedido(usuario);
        pedido.setStatus("PENDENTE");
        pedido = pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        // 3. Salvar os itens do pedido
        for (ItemPedidoRequestDTO itemDTO : pedidoRequestDTO.itens()){
            Produto produto = produtoRepository.findById(itemDTO.produtoId()).get();

            BigDecimal precoOriginal = produto.getPreco();
            Integer desconto = produto.getDesconto();
            BigDecimal precoUnitario = precoOriginal;
            if (desconto != null && desconto > 0) {
                precoUnitario = precoOriginal.multiply(BigDecimal.valueOf(100 - desconto))
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            }

            BigDecimal totalItem = precoUnitario.multiply(BigDecimal.valueOf(itemDTO.quantidade()));
            total = total.add(totalItem);

            ItemPedido itemPedido = new ItemPedido(
                    pedido,
                    produto,
                    itemDTO.quantidade(),
                    precoUnitario
            );
            itemPedidoRepository.save(itemPedido);
        }

        // 4. Remover os itens comprados do carrinho
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId()).orElse(null);
        if (carrinho != null) {
            List<Long> produtoIdsParaRemover = pedidoRequestDTO.itens().stream()
                    .map(ItemPedidoRequestDTO::produtoId)
                    .toList();
            carrinho.getItens().removeIf(item -> produtoIdsParaRemover.contains(item.getProduto().getId()));
            carrinhoRepository.save(carrinho);
        }

        pedido.setValorTotalPago(total);
        return pedidoRepository.save(pedido);
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
    public Pedido atualizarStatus(Long pedidoId, String novoStatus, String metodoPagamento, BigDecimal valorTotalPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido ID " + pedidoId + " não encontrado!"));

        String statusFormatado = novoStatus.trim().toUpperCase();

        // Se estiver concluindo o pedido (e ele não estava concluído antes), atualiza o estoque
        if ("CONCLUIDO".equals(statusFormatado) && !"CONCLUIDO".equals(pedido.getStatus())) {
            List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());
            
            // Validar se há estoque para todos os itens
            for (ItemPedido item : itens) {
                Produto produto = item.getProduto();
                if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                    throw new IllegalArgumentException("Não há estoque suficiente do produto: " + produto.getNome());
                }
            }

            // Deduzir o estoque
            for (ItemPedido item : itens) {
                Produto produto = item.getProduto();
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
                produtoRepository.save(produto);
            }
        }

        if (metodoPagamento != null) {
            pedido.setMetodoPagamento(metodoPagamento);
        }
        if (valorTotalPago != null) {
            pedido.setValorTotalPago(valorTotalPago);
        }
        pedido.setStatus(statusFormatado);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido finalizarCarrinho(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado!"));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }

        // 1. Validar estoque antes de formalizar o pedido
        for (var itemCarrinho : carrinho.getItens()) {
            Produto produto = itemCarrinho.getProduto();
            if (produto.getQuantidadeEstoque() < itemCarrinho.getQuantidade()) {
                throw new IllegalArgumentException("Não há estoque suficiente do produto: " + produto.getNome());
            }
        }

        // 2. Criar pedido com status PENDENTE
        Pedido pedido = new Pedido(usuario);
        pedido.setStatus("PENDENTE");
        pedido = pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        // 3. Salvar os itens do pedido
        for (var itemCarrinho : carrinho.getItens()) {
            Produto produto = itemCarrinho.getProduto();

            BigDecimal precoOriginal = produto.getPreco();
            Integer desconto = produto.getDesconto();
            BigDecimal precoUnitario = precoOriginal;
            if (desconto != null && desconto > 0) {
                precoUnitario = precoOriginal.multiply(BigDecimal.valueOf(100 - desconto))
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            }

            BigDecimal totalItem = precoUnitario.multiply(BigDecimal.valueOf(itemCarrinho.getQuantidade()));
            total = total.add(totalItem);

            ItemPedido itemPedido = new ItemPedido(
                    pedido,
                    produto,
                    itemCarrinho.getQuantidade(),
                    precoUnitario
            );
            itemPedidoRepository.save(itemPedido);
        }

        // 4. Limpar o carrinho
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        pedido.setValorTotalPago(total);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido ID " + id + " não encontrado!"));

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());

        List<ItemPedidoResponseDTO> itensDto = itens.stream()
                .map(ItemPedidoResponseDTO::new)
                .toList();

        return new PedidoResponseDTO(pedido, itensDto);
    }
}