package com.vesteBem.config;

import com.vesteBem.model.*;
import com.vesteBem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository,
            AvaliacaoRepository avaliacaoRepository,
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            CarrinhoRepository carrinhoRepository,
            PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private byte[] lerImagem(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Imagem não encontrada no classpath: " + resourcePath);
                return new byte[0];
            }
            return is.readAllBytes();
        } catch (IOException e) {
            System.err.println("Erro ao ler imagem: " + resourcePath);
            return new byte[0];
        }
    }

    private static class ProductSeed {
        String nome;
        String descricao;
        BigDecimal preco;
        int[] categoriaIndices;
        String[] imagens;
        Integer desconto;
        Boolean destaqueCarrossel;
        String imagemCarrosselPath;
        int estoque;

        ProductSeed(String nome, String descricao, String preco, int categoriaIndex,
                    String[] imagens, Integer desconto, Boolean destaqueCarrossel,
                    String imagemCarrosselPath, int estoque) {
            this.nome = nome;
            this.descricao = descricao;
            this.preco = new BigDecimal(preco);

            this.categoriaIndices = new int[]{categoriaIndex};

            this.imagens = imagens;
            this.desconto = desconto;
            this.destaqueCarrossel = destaqueCarrossel;
            this.imagemCarrosselPath = imagemCarrosselPath;
            this.estoque = estoque;
        }
    }

    @Override
    public void run(String... args) throws Exception {

        // ── 1. Categorias ──────────────────────────────────────────────────────────
        String[] catNames = {
                "Alfaiataria", // 0
                "Básicos Premium", // 1
                "Malhas & Tricot", // 2
                "Casacos & Blazers", // 3
                "Calças Clean", // 4
                "Camisaria Fina" // 5
        };
        String[] catImages = {
                "/img/cat_tailoring.png",
                "/img/cat_basics.png",
                "/img/cat_knitwear.png",
                "/img/cat_outerwear.png",
                "/img/cat_trousers.png",
                "/img/cat_shirts.png"
        };

        Categoria[] cats = new Categoria[catNames.length];
        for (int i = 0; i < catNames.length; i++) {
            final String nomeCat = catNames[i];
            final String imgCat = catImages[i];
            cats[i] = categoriaRepository.findByNome(nomeCat).orElseGet(() -> {
                Categoria c = new Categoria();
                c.setNome(nomeCat);
                c.setImagem(lerImagem(imgCat));
                c.setTipoImagem("image/png");
                return categoriaRepository.save(c);
            });
        }

        // ── 2. Usuários ────────────────────────────────────────────────────────────
        List<Usuario> clientes = new ArrayList<>();
        Usuario admin = null;

        if (usuarioRepository.count() == 0) {
            admin = new Usuario("Administrador", "admin@vestebem.com", passwordEncoder.encode("123456"));
            admin.setRole(UsuarioRole.ADMIN);
            admin = usuarioRepository.save(admin);

            String[][] userSeeds = {
                    { "Ana Beatriz Costa",      "ana@vestebem.com" },
                    { "Carlos Eduardo Lima",    "carlos@vestebem.com" },
                    { "Fernanda Oliveira",      "fernanda@vestebem.com" },
                    { "Rafael Souza",           "rafael@vestebem.com" },
                    { "Juliana Mendes",         "juliana@vestebem.com" },
                    { "Bruno Alves",            "bruno@vestebem.com" },
                    { "Camila Torres",          "camila@vestebem.com" },
                    { "Diego Martins",          "diego@vestebem.com" },
                    { "Larissa Rocha",          "larissa@vestebem.com" },
                    { "Thiago Barbosa",         "thiago@vestebem.com" },
                    { "Mariana Ferreira",       "mariana@vestebem.com" },
                    { "Lucas Pereira",          "lucas@vestebem.com" },
                    { "Isabela Carvalho",       "isabela@vestebem.com" },
                    { "Rodrigo Nascimento",     "rodrigo@vestebem.com" },
                    { "Beatriz Gomes",          "beatriz@vestebem.com" },
                    { "Henrique Teixeira",      "henrique@vestebem.com" },
                    { "Natália Ribeiro",        "natalia@vestebem.com" },
                    { "Felipe Cardoso",         "felipe@vestebem.com" },
                    { "Amanda Moreira",         "amanda@vestebem.com" },
                    { "Gabriel Araújo",         "gabriel@vestebem.com" }
            };

            for (String[] uSeed : userSeeds) {
                Usuario u = new Usuario(uSeed[0], uSeed[1], passwordEncoder.encode("123456"));
                u.setRole(UsuarioRole.CLIENTE);
                clientes.add(usuarioRepository.save(u));
            }
        } else {
            admin = usuarioRepository.findByEmail("admin@vestebem.com").orElse(null);
            String[] emails = {
                    "ana@vestebem.com",      "carlos@vestebem.com",   "fernanda@vestebem.com",
                    "rafael@vestebem.com",   "juliana@vestebem.com",  "bruno@vestebem.com",
                    "camila@vestebem.com",   "diego@vestebem.com",    "larissa@vestebem.com",
                    "thiago@vestebem.com",   "mariana@vestebem.com",  "lucas@vestebem.com",
                    "isabela@vestebem.com",  "rodrigo@vestebem.com",  "beatriz@vestebem.com",
                    "henrique@vestebem.com", "natalia@vestebem.com",  "felipe@vestebem.com",
                    "amanda@vestebem.com",   "gabriel@vestebem.com"
            };
            for (String email : emails) {
                usuarioRepository.findByEmail(email).ifPresent(clientes::add);
            }
        }

        // ── 3. Produtos (20) ───────────────────────────────────────────────────────
        List<Produto> produtosSalvos = new ArrayList<>();

        if (produtoRepository.count() == 0) {
            ProductSeed[] seeds = {
                    // ── Alfaiataria (0) ─────────────────────────────────────────────
                    new ProductSeed(
                            "Blazer Slim Lã Fria Grafite",
                            "Blazer estruturado com corte slim, lapela chanfrada e abotoamento simples. " +
                                    "Confeccionado em lã fria premium italiana, perfeito para composições formais elegantes.",
                            "1290.00", 0, new String[] {
                                    "/img/blazer_1.png",
                                    "/img/blazer_2.png",
                                    "/img/blazer_3.png"
                            }, 10, true, "/img/blazer_1.png", 18),

                    new ProductSeed(
                            "Colete Alfaiataria Minimalista Preto",
                            "Colete clássico com ajuste traseiro em fivela e abotoamento frontal embutido. " +
                                    "Ideal para sobreposições elegantes ou uso como peça única versátil.",
                            "359.00", 0, new String[] {
                                    "/img/coletepreto_1.png",
                                    "/img/coletepreto_2.png",
                                    "/img/coletepreto_3.png"
                            }, 0, false, null, 30),

                    new ProductSeed(
                            "Paletó Clássico Crepe Preto",
                            "Paletó em crepe encorpado de caimento pesado, lapelas de cetim e modelagem " +
                                    "levemente oversized, unindo modernidade e rigor clássico.",
                            "1490.00", 0, new String[] {
                                    "/img/paletopreto_1.png",
                                    "/img/paletopreto_2.png",
                                    "/img/paletopreto_3.png"
                            }, 15, false, null, 12),

                    new ProductSeed(
                            "Calça Cigarrete Lã Fria Off-White",
                            "Calça cigarrete estruturada em alfaiataria fina, com zíperes invisíveis nas barras " +
                                    "e toque macio ao corpo. Um básico atemporal da alfaiataria.",
                            "620.00", 0, new String[] {
                                    "/img/calcacigarrete_1.png",
                                    "/img/calcacigarrete_2.png",
                                    "/img/calcacigarrete_3.png"
                            }, 0, false, null, 25),

                    // ── Básicos Premium (1) ──────────────────────────────────────────
                    new ProductSeed(
                            "Camiseta Algodão Egípcio Nude",
                            "Camiseta clássica confeccionada em algodão egípcio de fibra longa. " +
                                    "Toque extremamente suave, caimento fluido e gola sutilmente estruturada.",
                            "189.00", 1, new String[] {
                                    "/img/camisetanude_1.png",
                                    "/img/camisetanude_2.png",
                                    "/img/camisetanude_3.png"
                            }, 0, false, null, 60),

                    new ProductSeed(
                            "T-Shirt Pima Premium Preta",
                            "Camiseta premium em algodão pima peruano, conhecida por sua durabilidade, " +
                                    "brilho suave e maciez inigualável. Um essencial do guarda-roupa minimalista.",
                            "199.00", 1, new String[] {
                                    "/img/camisetapreta_1.png",
                                    "/img/camisetapreta_2.png",
                                    "/img/camisetapreta_3.png"
                            }, 0, true, "/img/camisetapreta_1.png", 45),

                    new ProductSeed(
                            "Body Canelado Minimalista Areia",
                            "Body canelado em malha premium com elastano, decote quadrado e fecho inferior " +
                                    "confortável. Desenha a silhueta de forma clean e elegante.",
                            "229.00", 1, new String[] {
                                    "/img/bodyareia_1.png",
                                    "/img/bodyareia_2.png",
                                    "/img/bodyareia_3.png"
                            }, 20, false, null, 35),

                    // ── Malhas & Tricot (2) ──────────────────────────────────────────
                    new ProductSeed(
                            "Suéter Cashmere Gola Alta Camel",
                            "Suéter luxuoso em puro cashmere mongol. Gola alta aconchegante, " +
                                    "punhos canelados e caimento clássico e atemporal. O máximo em conforto.",
                            "1890.00", 2, new String[] {
                                    "/img/sueter_1.png",
                                    "/img/sueter_2.png",
                                    "/img/sueter_3.png"
                            }, 25, true, "/img/sueter_1.png", 8),

                    new ProductSeed(
                            "Cardigan Tricot Oversized Gelo",
                            "Cardigan amplo em tricot texturizado com botões de chifre natural. " +
                                    "Perfeito para sobreposições modernas e aconchegantes nos dias mais frios.",
                            "589.00", 2, new String[] {
                                    "/img/cardigangelo_1.png",
                                    "/img/cardigangelo_2.png",
                                    "/img/cardigangelo_3.png"
                            }, 0, false, null, 20),

                    new ProductSeed(
                            "Blusa Tricot Gola Canoa Preta",
                            "Blusa de tricot fino com decote canoa e mangas 7/8. " +
                                    "Visual clean e sofisticado para noites frescas e composições de inverno.",
                            "349.00", 2, new String[] {
                                    "/img/golacanoa_1.png",
                                    "/img/golacanoa_2.png",
                                    "/img/golacanoa_3.png"
                            }, 10, false, null, 28),

                    // ── Casacos & Blazers (3) ────────────────────────────────────────
                    new ProductSeed(
                            "Sobretudo Lã e Alpaca Camel",
                            "Sobretudo longo trespassado confeccionado em mescla nobre de lã virgem e alpaca. " +
                                    "Bolsos embutidos e faixa opcional para amarração. Um investimento atemporal.",
                            "2490.00", 3, new String[] {
                                    "/img/sobretudocamel_1.png",
                                    "/img/sobretudocamel_2.png",
                                    "/img/sobretudocamel_3.png"
                            }, 30, true, "/img/sobretudocamel_1.png", 6),

                    new ProductSeed(
                            "Trench Coat Impermeável Classic",
                            "O clássico casaco de chuva em sarja impermeável com abotoamento duplo, " +
                                    "lapelas largas e cinto ajustável. Referência de estilo britânico.",
                            "1290.00", 3, new String[] {
                                    "/img/trenchcoat_1.png",
                                    "/img/trenchcoat_2.png",
                                    "/img/trenchcoat_3.png"
                            }, 0, false, null, 10),

                    new ProductSeed(
                            "Blazer Oversized Linho Preto",
                            "Blazer com modelagem ampla e moderna, confeccionado em linho belga estruturado " +
                                    "com forro parcial respirável. Versátil para looks formais e casuais.",
                            "789.00", 3, new String[] {
                                    "/img/blazeroversized_1.png",
                                    "/img/blazeroversized_2.png",
                                    "/img/blazeroversized_3.png"
                            }, 15, false, null, 22),

                    // ── Calças Clean (4) ─────────────────────────────────────────────
                    new ProductSeed(
                            "Calça Reta Gabardine Bege",
                            "Calça clássica de gabardine de algodão com corte reto impecável, " +
                                    "ideal para compor com camisas, blazers e sapatos de couro.",
                            "399.00", 4, new String[] {
                                    "/img/calcabege_1.png",
                                    "/img/calcabege_2.png",
                                    "/img/calcabege_3.png"
                            }, 0, false, null, 40),

                    new ProductSeed(
                            "Pantalona Fluida Crepe Preta",
                            "Pantalona de cintura alta com caimento super fluido, cós largo estruturado " +
                                    "e bolsos faca nas laterais. O equilíbrio perfeito entre conforto e elegância.",
                            "459.00", 4, new String[] {
                                    "/img/pantalonepreta_1.png",
                                    "/img/pantalonepreta_2.png",
                                    "/img/pantalonepreta_3.png"
                            }, 15, false, null, 32),

                    new ProductSeed(
                            "Calça Wide Leg Linho Off-White",
                            "Calça de linho com modelagem wide leg, pregas frontais e caimento leve. " +
                                    "Perfeita para os dias de verão com composições leves e sofisticadas.",
                            "520.00", 4, new String[] {
                                    "/img/wideleg_1.png",
                                    "/img/wideleg_2.png",
                                    "/img/wideleg_3.png"
                            }, 0, false, null, 28),

                    // ── Camisaria Fina (5) ───────────────────────────────────────────
                    new ProductSeed(
                            "Camisa Seda Pura Branca",
                            "Camisa clássica de botões em seda pura premium. Caimento fluido, " +
                                    "punhos largos e botões de madrepérola legítima. O ápice da camisaria.",
                            "1190.00", 5, new String[] {
                                    "/img/camisabranca_1.png",
                                    "/img/camisabranca_2.png",
                                    "/img/camisabranca_3.png"
                            }, 20, true, "/img/camisabranca_1.png", 15),

                    new ProductSeed(
                            "Camisa Linho Premium Azul Claríssimo",
                            "Camisa casual chique em linho belga amaciado. Colarinho levemente desestruturado " +
                                    "e toque arejado. Perfeita para looks de verão com estilo.",
                            "389.00", 5, new String[] {
                                    "/img/camisaazul_1.png",
                                    "/img/camisaazul_2.png",
                                    "/img/camisaazul_3.png"
                            }, 0, false, null, 38),

                    new ProductSeed(
                            "Camisa Algodão Egípcio Preta",
                            "Camisa formal em tricoline de algodão egípcio fio 120. " +
                                    "Caimento estruturado e acabamento impecável para ocasiões especiais.",
                            "489.00", 5, new String[] {
                                    "/img/camisapreta_1.png",
                                    "/img/camisapreta_2.png",
                                    "/img/camisapreta_3.png"
                            }, 0, false, null, 20),

                    new ProductSeed(
                            "Chemise Midi Linho Cru",
                            "Vestido camisa com comprimento midi, abotoamento frontal completo e cinto " +
                                    "de mesmo tecido para ajuste. Transita entre o casual e o formal com elegância.",
                            "590.00", 5, new String[] {
                                    "/img/midlinho_1.png",
                                    "/img/midlinho_2.png",
                                    "/img/midlinho_3.png"
                            }, 10, false, null, 18)
            };

            for (ProductSeed seed : seeds) {
                Produto p = new Produto(seed.nome, seed.descricao, seed.preco, seed.estoque);
                p.setDesconto(seed.desconto);
                p.setDestaqueCarrossel(seed.destaqueCarrossel);
                if (seed.imagemCarrosselPath != null) {
                    p.setImagemCarrossel(lerImagem(seed.imagemCarrosselPath));
                    p.setTipoImagemCarrossel("image/png");
                }
                List<Categoria> prodCats = new ArrayList<>();
                for (int ci : seed.categoriaIndices) { prodCats.add(cats[ci]); }
                p.setCategorias(prodCats);

                for (String imgPath : seed.imagens) {
                    ProdutoImagem pImg = new ProdutoImagem();
                    pImg.setDados(lerImagem(imgPath));
                    pImg.setTipo("image/png");
                    pImg.setProduto(p);
                    p.getImagens().add(pImg);
                }

                p = produtoRepository.save(p);
                produtosSalvos.add(p);
            }
        } else {
            produtosSalvos.addAll(produtoRepository.findAll());
        }

        // ── 4. Pedidos aleatórios por usuário ──────────────────────────────────────
        // Mapa produto -> lista de usuários que o compraram (para controle de avaliações)
        java.util.Map<Long, List<Usuario>> compradores = new java.util.HashMap<>();
        for (Produto p : produtosSalvos) {
            compradores.put(p.getId(), new ArrayList<>());
        }

        if (!clientes.isEmpty() && !produtosSalvos.isEmpty() && pedidoRepository.count() == 0) {
            Random rand = new Random(42L);
            String[] statusPool = { "PENDENTE", "PROCESSANDO", "CONCLUIDO", "CONCLUIDO", "CONCLUIDO", "CONCLUIDO" };

            for (Usuario cliente : clientes) {
                int numPedidos = 1 + rand.nextInt(4); // 1 a 4 pedidos por usuário
                for (int p = 0; p < numPedidos; p++) {
                    Pedido pedido = new Pedido(cliente);
                    String status = statusPool[rand.nextInt(statusPool.length)];
                    pedido.setStatus(status);
                    pedido.setDataCriacao(LocalDateTime.now().minusDays(1 + rand.nextInt(120))
                            .minusHours(rand.nextInt(24)));
                    pedido = pedidoRepository.save(pedido);

                    // 1 a 4 itens distintos por pedido
                    List<Produto> shuffled = new ArrayList<>(produtosSalvos);
                    Collections.shuffle(shuffled, rand);
                    int numItens = 1 + rand.nextInt(4);
                    for (int i = 0; i < Math.min(numItens, shuffled.size()); i++) {
                        Produto prod = shuffled.get(i);
                        int qty = 1 + rand.nextInt(3);
                        ItemPedido item = new ItemPedido(pedido, prod, qty, prod.getPreco());
                        itemPedidoRepository.save(item);

                        // Registrar que este cliente comprou este produto
                        List<Usuario> lista = compradores.get(prod.getId());
                        if (lista != null && !lista.contains(cliente)) {
                            lista.add(cliente);
                        }
                    }
                }
            }
        } else if (pedidoRepository.count() > 0) {
            // Banco já populado: reconstruir mapa de compradores a partir dos pedidos existentes
            for (Pedido pedido : pedidoRepository.findAll()) {
                for (ItemPedido item : itemPedidoRepository.findByPedidoId(pedido.getId())) {
                    List<Usuario> lista = compradores.get(item.getProduto().getId());
                    if (lista != null && !lista.contains(pedido.getUsuario())) {
                        lista.add(pedido.getUsuario());
                    }
                }
            }
        }

        // ── 5. Avaliações (3–15 por produto, apenas compradores) ───────────────────
        if (!clientes.isEmpty() && avaliacaoRepository.count() == 0) {
            for (Produto produto : produtosSalvos) {
                List<Usuario> elegíveis = compradores.getOrDefault(produto.getId(), new ArrayList<>());
                // Fallback: se nenhum comprador registrado, usa todos os clientes
                if (elegíveis.isEmpty()) elegíveis = clientes;
                gerarAvaliacoes(produto, elegíveis);
            }
        }

        // ── 6. Carrinhos para alguns usuários (primeiros 8) ────────────────────────
        if (!clientes.isEmpty() && !produtosSalvos.isEmpty() && carrinhoRepository.count() == 0) {
            Random rand = new Random(7L);
            int usersWithCart = Math.min(8, clientes.size());
            for (int i = 0; i < usersWithCart; i++) {
                Usuario usuario = clientes.get(i);
                Carrinho carrinho = new Carrinho(usuario);
                carrinho = carrinhoRepository.save(carrinho);

                // Adicionar 1 a 4 produtos distintos no carrinho
                List<Produto> shuffled = new ArrayList<>(produtosSalvos);
                Collections.shuffle(shuffled, rand);
                int numItens = 1 + rand.nextInt(4);
                for (int j = 0; j < Math.min(numItens, shuffled.size()); j++) {
                    Produto prod = shuffled.get(j);
                    int qty = 1 + rand.nextInt(3);
                    ItemCarrinho itemCarrinho = new ItemCarrinho(carrinho, prod, qty);
                    carrinho.getItens().add(itemCarrinho);
                }
                carrinhoRepository.save(carrinho);
            }
        }
    }

    private void gerarAvaliacoes(Produto produto, List<Usuario> clientes) {
        String[] openers = {
                "Simplesmente incrível.", "Peça maravilhosa!", "Estou sem palavras para a qualidade.",
                "Uma das melhores aquisições do ano.", "Minimalismo na sua melhor definição.",
                "Design limpo e sofisticado.", "Perfeito para qualquer ocasião formal ou casual chique.",
                "Essa marca nunca decepciona.", "Caimento impecável e modelagem excelente.",
                "Muito elegante e atemporal."
        };
        String[] middles = {
                " O toque do tecido é extremamente suave e premium.",
                " A qualidade do tecido superou minhas expectativas mais altas.",
                " Costuras muito bem feitas e acabamento de altíssimo nível.",
                " Veste super bem no corpo, valorizando a silhueta de forma sutil.",
                " A cor é muito bonita e o material é super confortável e leve.",
                " O corte reto e clean é exatamente o que eu buscava.",
                " Percebe-se de longe que é um produto de luxo.",
                " O caimento nos ombros e cintura ficou perfeito.",
                " O material é nobre e respira muito bem.",
                " Dá para sentir o alto padrão de costura em cada detalhe."
        };
        String[] closers = {
                " Vale cada centavo investido.", " Recomendo de olhos fechados.",
                " Com certeza comprarei outras cores.", " Se tornou minha peça favorita.",
                " Excelente custo-benefício para um produto premium.",
                " A entrega foi super rápida e a embalagem veio impecável.",
                " Ideal para quem gosta de um estilo clean e minimalista.",
                " Muito satisfeito com a compra!",
                " Uma peça indispensável no guarda-roupa.",
                " Um clássico atemporal que dura a vida toda."
        };

        Random rand = new Random(produto.getNome().hashCode());
        // 3 a 15 avaliações por produto
        int numReviews = 3 + rand.nextInt(13);

        for (int i = 0; i < numReviews; i++) {
            Usuario usuario = clientes.get(rand.nextInt(clientes.size()));

            double rating;
            int r = rand.nextInt(100);
            if (r < 55)
                rating = 5.0;
            else if (r < 80)
                rating = 4.5;
            else if (r < 93)
                rating = 4.0;
            else if (r < 98)
                rating = 3.5;
            else
                rating = 3.0;

            String comment = openers[rand.nextInt(openers.length)]
                    + middles[rand.nextInt(middles.length)]
                    + closers[rand.nextInt(closers.length)];

            Avaliacao av = new Avaliacao(produto, usuario, rating, comment);
            av.setDataAvaliacao(LocalDateTime.now().minusDays(rand.nextInt(90)));
            avaliacaoRepository.save(av);
        }
    }
}