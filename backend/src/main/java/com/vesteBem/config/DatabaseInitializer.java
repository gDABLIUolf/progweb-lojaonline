package com.vesteBem.config;

import com.vesteBem.model.*;
import com.vesteBem.repository.CategoriaRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import com.vesteBem.repository.AvaliacaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(CategoriaRepository categoriaRepository,
                               ProdutoRepository produtoRepository,
                               UsuarioRepository usuarioRepository,
                               AvaliacaoRepository avaliacaoRepository,
                               PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
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
        int categoriaIndex;
        String[] imagens;

        ProductSeed(String nome, String descricao, String preco, int categoriaIndex, String[] imagens) {
            this.nome = nome;
            this.descricao = descricao;
            this.preco = new BigDecimal(preco);
            this.categoriaIndex = categoriaIndex;
            this.imagens = imagens;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicializar as 10 Categorias de Luxo com Imagens Exclusivas
        Categoria[] cats = new Categoria[10];
        String[] catNames = new String[] {
            "Alfaiataria",
            "Básicos Premium",
            "Malhas & Tricot",
            "Casacos & Blazers",
            "Calças Clean",
            "Camisaria Fina",
            "Vestidos Minimalistas",
            "Calçados de Couro",
            "Acessórios Essenciais",
            "Loungewear de Luxo"
        };
        String[] catImages = new String[] {
            "/img/cat_tailoring.png", // Alfaiataria
            "/img/cat_basics.png", // Básicos Premium
            "/img/cat_knitwear.png", // Malhas & Tricot
            "/img/cat_outerwear.png", // Casacos & Blazers
            "/img/cat_trousers.png", // Calças Clean
            "/img/cat_shirts.png", // Camisaria Fina
            "/img/cat_dresses.png", // Vestidos Minimalistas
            "/img/cat_footwear.png", // Calçados de Couro
            "/img/cat_accessories.png", // Acessórios Essenciais
            "/img/cat_loungewear.png"  // Loungewear de Luxo
        };

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

        // 2. Inicializar Usuários
        List<Usuario> clientes = new ArrayList<>();
        Usuario admin = null;

        if (usuarioRepository.count() == 0) {
            admin = new Usuario("Administrador", "admin@vestebem.com", passwordEncoder.encode("123456"));
            admin.setRole(UsuarioRole.ADMIN);
            admin = usuarioRepository.save(admin);

            String[][] userSeeds = new String[][] {
                {"João Silva", "cliente@vestebem.com"},
                {"Maria Souza", "maria@vestebem.com"},
                {"Pedro Santos", "pedro@vestebem.com"},
                {"Lucas Lima", "lucas@vestebem.com"},
                {"Isabella Rocha", "isabella@vestebem.com"},
                {"Sophia Alencar", "sophia@vestebem.com"},
                {"Gabriel Costa", "gabriel@vestebem.com"},
                {"Bernardo Gomes", "bernardo@vestebem.com"},
                {"Valentina Mendes", "valentina@vestebem.com"},
                {"Matheus Oliveira", "matheus@vestebem.com"},
                {"Alice Ribeiro", "alice@vestebem.com"},
                {"Enzo Barbosa", "enzo@vestebem.com"}
            };

            for (String[] uSeed : userSeeds) {
                Usuario u = new Usuario(uSeed[0], uSeed[1], passwordEncoder.encode("123456"));
                u.setRole(UsuarioRole.CLIENTE);
                clientes.add(usuarioRepository.save(u));
            }
        } else {
            admin = usuarioRepository.findByEmail("admin@vestebem.com").orElse(null);
            String[] emails = new String[] {
                "cliente@vestebem.com", "maria@vestebem.com", "pedro@vestebem.com", "lucas@vestebem.com",
                "isabella@vestebem.com", "sophia@vestebem.com", "gabriel@vestebem.com", "bernardo@vestebem.com",
                "valentina@vestebem.com", "matheus@vestebem.com", "alice@vestebem.com", "enzo@vestebem.com"
            };
            for (String email : emails) {
                usuarioRepository.findByEmail(email).ifPresent(clientes::add);
            }
        }

        // 3. Inicializar 50 Produtos Premium de Luxo e suas Galerias de Imagens
        if (produtoRepository.count() == 0) {
            ProductSeed[] seeds = new ProductSeed[] {
                // 1-5 Alfaiataria (0)
                new ProductSeed("Blazer Slim Lã Fria Grafite", "Blazer estruturado com corte slim, lapela chanfrada e abotoamento simples. Confeccionado em lã fria premium italiana, perfeito para composições formais elegantes.", "1290.00", 0, new String[]{"/img/prod_gray_blazer.png"}),
                new ProductSeed("Calça Alfaiataria Linho Cru", "Calça de alfaiataria com corte reto, bolsos faca e vinco marcado. Feita em linho puro respirável, oferecendo leveza e sofisticação natural.", "489.00", 0, new String[]{"/img/cat_trousers.png"}),
                new ProductSeed("Colete Alfaiataria Minimalista Preto", "Colete clássico com ajuste traseiro em fivela e abotoamento frontal embutido. Ideal para sobreposições elegantes ou uso como peça única.", "359.00", 0, new String[]{"/img/cat_tailoring.png"}),
                new ProductSeed("Paletó Clássico Crepe Preto", "Paletó em crepe encorpado de caimento pesado, lapelas de cetim e modelagem levemente oversized, unindo modernidade e rigor clássico.", "1490.00", 0, new String[]{"/img/cat_tailoring.png", "/img/prod_gray_blazer.png"}),
                new ProductSeed("Calça Cigarrete Lã Fria Off-White", "Calça cigarrete estruturada em alfaiataria fina, com zíperes invisíveis nas barras e toque macio ao corpo.", "620.00", 0, new String[]{"/img/cat_trousers.png"}),
                
                // 6-10 Básicos Premium (1)
                new ProductSeed("Camiseta Algodão Egípcio Nude", "Camiseta clássica confeccionada em algodão egípcio de fibra longa. Toque extremamente suave, caimento fluido e gola sutilmente estruturada.", "189.00", 1, new String[]{"/img/cat_basics.png"}),
                new ProductSeed("Regata Silk-Modal Off-White", "Regata minimalista em mescla de seda e modal. Toque sedoso, caimento levemente solto e decote em U elegante.", "149.00", 1, new String[]{"/img/cat_basics.png"}),
                new ProductSeed("T-Shirt Pima Premium Preta", "Camiseta premium em algodão pima peruano, conhecida por sua durabilidade, brilho suave e maciez inigualável.", "199.00", 1, new String[]{"/img/prod_black_tshirt.png"}),
                new ProductSeed("Body Canelado Minimalista Areia", "Body canelado em malha premium com elastano, decote quadrado e fecho inferior confortável. Desenha a silhueta de forma clean.", "229.00", 1, new String[]{"/img/cat_basics.png"}),
                new ProductSeed("Camiseta Gola Alta Pima Cinza", "Camiseta com gola alta ajustada, confeccionada em algodão pima peruano de alta densidade.", "249.00", 1, new String[]{"/img/cat_basics.png", "/img/prod_black_tshirt.png"}),

                // 11-15 Malhas & Tricot (2)
                new ProductSeed("Suéter Cashmere Gola Alta Camel", "Suéter luxuoso em puro cashmere mongol. Gola alta aconchegante, punhos canelados e caimento clássico e atemporal.", "1890.00", 2, new String[]{"/img/cat_knitwear.png"}),
                new ProductSeed("Cardigan Tricot Oversized Gelo", "Cardigan amplo em tricot texturizado com botões de chifre natural. Perfeito para sobreposições modernas e aconchegantes.", "589.00", 2, new String[]{"/img/cat_knitwear.png"}),
                new ProductSeed("Suéter Lã Merino Canelado Off-White", "Suéter com gola redonda em lã merino extrafina canelada, unindo aquecimento leve e toque extremamente suave.", "790.00", 2, new String[]{"/img/cat_knitwear.png"}),
                new ProductSeed("Blusa Tricot Gola Canoa Preta", "Blusa de tricot fino com decote canoa e mangas 7/8. Visual clean e sofisticado para noites frescas.", "349.00", 2, new String[]{"/img/cat_knitwear.png"}),
                new ProductSeed("Vestido Midi Tricot Canelado Areia", "Vestido midi de tricot canelado que delineia o corpo com extremo conforto e sofisticação minimalista.", "499.00", 2, new String[]{"/img/cat_knitwear.png"}),

                // 16-20 Casacos & Blazers (3)
                new ProductSeed("Sobretudo Lã e Alpaca Camel", "Sobretudo longo trespassado confeccionado em mescla nobre de lã virgem e alpaca. Bolsos embutidos e faixa opcional para amarração.", "2490.00", 3, new String[]{"/img/prod_camel_coat.png"}),
                new ProductSeed("Trench Coat Impermeável Fendi", "O clássico casaco de chuva em sarja impermeável com abotoamento duplo, lapelas largas e cinto ajustável.", "1290.00", 3, new String[]{"/img/cat_outerwear.png"}),
                new ProductSeed("Jaqueta Couro Pelica Minimalista Preta", "Jaqueta de couro legítimo extremamente macio, com ferragens em chumbo escuro fosco e silhueta limpa.", "1990.00", 3, new String[]{"/img/cat_outerwear.png"}),
                new ProductSeed("Blazer Oversized Linho Preto", "Blazer com modelagem ampla e moderna, confeccionado em linho belga estruturado com forro parcial respirável.", "789.00", 3, new String[]{"/img/prod_gray_blazer.png"}),
                new ProductSeed("Bomber Satin de Seda Cinza", "Jaqueta estilo bomber confeccionada em cetim de seda encorpado, com acabamentos elásticos discretos e toque acetinado.", "980.00", 3, new String[]{"/img/cat_outerwear.png", "/img/prod_camel_coat.png"}),

                // 21-25 Calças Clean (4)
                new ProductSeed("Calça Reta Gabardine Bege", "Calça clássica de gabardine de algodão com corte reto impecável, ideal para compor com camisas e blazers.", "399.00", 4, new String[]{"/img/cat_trousers.png"}),
                new ProductSeed("Pantalona Fluida Crepe Preta", "Pantalona de cintura alta com caimento super fluido, cós largo estruturado e bolsos faca nas laterais.", "459.00", 4, new String[]{"/img/cat_trousers.png"}),
                new ProductSeed("Calça Wide Leg Linho Off-White", "Calça de linho com modelagem wide leg, pregas frontais e caimento leve. Perfeita para os dias de verão.", "520.00", 4, new String[]{"/img/cat_trousers.png"}),
                new ProductSeed("Calça Utility Minimalista Oliva", "Calça de sarja de algodão de alta densidade com bolsos cargo ultra-discretos e costuras reforçadas.", "419.00", 4, new String[]{"/img/cat_trousers.png"}),
                new ProductSeed("Calça Jogger Seda Chumbo", "Calça estilo jogger com punhos em elástico embutido, cós com cordão de seda e caimento luxuoso.", "890.00", 4, new String[]{"/img/cat_trousers.png"}),

                // 26-30 Camisaria Fina (5)
                new ProductSeed("Camisa Seda Pura Branca", "Camisa clássica de botões em seda pura premium. Caimento fluido, punhos largos e botões de madrepérola legítima.", "1190.00", 5, new String[]{"/img/cat_shirts.png"}),
                new ProductSeed("Camisa Linho Premium Azul Claríssimo", "Camisa casual chique em linho belga amaciado. Colarinho levemente desestruturado e toque arejado.", "389.00", 5, new String[]{"/img/prod_blue_shirt.png"}),
                new ProductSeed("Camisa Algodão Egípcio Preta", "Camisa formal em tricoline de algodão egípcio fio 120. Caimento estruturado e acabamento impecável.", "489.00", 5, new String[]{"/img/cat_shirts.png"}),
                new ProductSeed("Chemise Midi Linho Cru", "Vestido camisa com comprimento midi, abotoamento frontal completo e cinto de mesmo tecido para ajuste.", "590.00", 5, new String[]{"/img/cat_shirts.png", "/img/prod_blue_shirt.png"}),
                new ProductSeed("Camisa Algodão Cetim Off-White", "Camisa de algodão premium com acabamento acetinado que reflete a luz sutilmente. Visual luxuoso.", "450.00", 5, new String[]{"/img/cat_shirts.png"}),

                // 31-35 Vestidos Minimalistas (6)
                new ProductSeed("Vestido Longo Crepe Minimal Preto", "Vestido de festa longo com corte reto e fenda traseira discreta. Costas abertas com alças finas elegantes.", "1390.00", 6, new String[]{"/img/prod_black_dress.png"}),
                new ProductSeed("Vestido Midi Gola Alta Silk Off-White", "Vestido midi sem mangas com gola alta drapeada e caimento enviesado que acompanha o movimento.", "890.00", 6, new String[]{"/img/cat_dresses.png"}),
                new ProductSeed("Vestido Slip Dress Seda Champanhe", "O slip dress definitivo em seda pura, com decote bobo discreto e alças reguláveis. Um clássico chique.", "1290.00", 6, new String[]{"/img/cat_dresses.png"}),
                new ProductSeed("Vestido Cache-Coeur Linho Areia", "Vestido transpassado midi de linho estruturado, com amarração lateral interna e caimento evasê limpo.", "659.00", 6, new String[]{"/img/cat_dresses.png"}),
                new ProductSeed("Vestido Alfaiataria Reta Chumbo", "Vestido estruturado sem mangas de corte geométrico reto, perfeito para combinar com camisas brancas.", "790.00", 6, new String[]{"/img/cat_dresses.png", "/img/prod_black_dress.png"}),

                // 36-40 Calçados de Couro (7)
                new ProductSeed("Scarpin Couro Macio Off-White", "Scarpin com salto fino médio, bico fino clássico e palmilha acolchoada revestida em pelica premium.", "890.00", 7, new String[]{"/img/cat_footwear.png"}),
                new ProductSeed("Loafer Minimalista Couro Preto", "Mocassim clássico de couro texturizado, solado de couro com inserto de borracha e construção artesanal.", "750.00", 7, new String[]{"/img/cat_footwear.png"}),
                new ProductSeed("Bota Cano Curto Pelica Camel", "Bota elegante com bico quadrado suave, salto bloco revestido de couro e zíper interno invisível.", "1190.00", 7, new String[]{"/img/cat_footwear.png"}),
                new ProductSeed("Sandália Rasteira Couro Cru", "Sandália rasteira de tiras finas de couro natural e fechamento por fivela minimalista no calcanhar.", "450.00", 7, new String[]{"/img/cat_footwear.png"}),
                new ProductSeed("Mule Couro Texturizado Nude", "Sapatilha mule aberta no calcanhar, confeccionada em couro texturizado de altíssima maciez.", "590.00", 7, new String[]{"/img/cat_footwear.png"}),

                // 41-45 Acessórios Essenciais (8)
                new ProductSeed("Bolsa Baguette Couro Estruturada", "Bolsa de couro rígido com alça curta ajustável, ferragens escovadas discretas e fecho magnético.", "1590.00", 8, new String[]{"/img/cat_accessories.png"}),
                new ProductSeed("Cinto Couro Fivela Oculta Preto", "Cinto de couro de bezerro com largura média e fivela de encaixe oculta para um visual limpo.", "290.00", 8, new String[]{"/img/cat_accessories.png"}),
                new ProductSeed("Óculos de Sol Acetato Preto", "Óculos de sol geométrico clássico em acetato polido à mão com lentes Zeiss de alta proteção.", "690.00", 8, new String[]{"/img/cat_accessories.png"}),
                new ProductSeed("Echarpe Cashmere Gelo", "Echarpe tecida em puro cashmere de gramatura leve, com barras levemente desfiadas à mão.", "580.00", 8, new String[]{"/img/cat_accessories.png"}),
                new ProductSeed("Carteira Slim Couro Café", "Carteira compacta estruturada com divisões para cartões e compartimento central de notas.", "320.00", 8, new String[]{"/img/cat_accessories.png"}),

                // 46-50 Loungewear de Luxo (9)
                new ProductSeed("Roupão Algodão Penteado Waffle Gelo", "Roupão de banho longo waffle premium, pré-lavado e amaciado, ideal para momentos pós-banho.", "480.00", 9, new String[]{"/img/cat_loungewear.png"}),
                new ProductSeed("Conjunto Seda Pura Camisa/Calça", "Pijama de luxo composto por camisa de manga longa e calça fluida em seda pura com vivo contrastante.", "1890.00", 9, new String[]{"/img/cat_loungewear.png", "/img/cat_trousers.png"}),
                new ProductSeed("Cardigan Canelado Knit Areia", "Cardigan de loungewear ultra macio em tricot canelado leve, ideal para o conforto de casa.", "390.00", 9, new String[]{"/img/cat_loungewear.png"}),
                new ProductSeed("Calça Jogger Knit Premium Gelo", "Calça jogger de tricot de viscose e elastano, com caimento macio e cós ajustável super confortável.", "350.00", 9, new String[]{"/img/cat_loungewear.png"}),
                new ProductSeed("Shorts Silk-Modal Comfort Nude", "Shorts leve com cós elástico revestido, feito em modal e seda para maciez extrema e frescor.", "220.00", 9, new String[]{"/img/cat_loungewear.png"})
            };

            for (ProductSeed seed : seeds) {
                Produto p = new Produto(seed.nome, seed.descricao, seed.preco, 50);
                p.setCategorias(List.of(cats[seed.categoriaIndex]));

                for (String imgPath : seed.imagens) {
                    ProdutoImagem pImg = new ProdutoImagem();
                    pImg.setDados(lerImagem(imgPath));
                    pImg.setTipo("image/png");
                    pImg.setProduto(p);
                    p.getImagens().add(pImg);
                }

                p = produtoRepository.save(p);

                // Gerar 30 avaliações para cada produto
                generateReviewsForProduct(p, clientes);
            }
        }
    }

    private void generateReviewsForProduct(Produto produto, List<Usuario> clientes) {
        if (clientes.isEmpty()) return;

        String[] openers = {
            "Simplesmente incrível.", "Peça maravilhosa!", "Estou sem palavras para a qualidade.",
            "Uma das melhores aquisições do ano.", "Minimalismo na sua melhor definição.",
            "Design limpo e sofisticado.", "Perfeito para qualquer ocasião formal ou casual chique.",
            "Essa marca nunca decepciona.", "Caimento impecável e modelagem excelente.", "Muito elegante e atemporal."
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
            " Com certeza comprarei outras cores/peças.", " Se tornou minha peça favorita.",
            " Excelente custo-benefício para um produto premium.",
            " A entrega foi super rápida e a embalagem veio impecável.",
            " Ideal para quem gosta de um estilo clean e minimalista.", " Muito satisfeito com a compra!",
            " Uma peça indispensável no guarda-roupa.", " Um clássico atemporal que dura a vida toda."
        };

        int numReviews = 30;
        Random rand = new Random(produto.getNome().hashCode());

        for (int i = 0; i < numReviews; i++) {
            Usuario usuario = clientes.get(rand.nextInt(clientes.size()));

            double rating;
            int r = rand.nextInt(100);
            if (r < 60) {
                rating = 5.0;
            } else if (r < 90) {
                rating = 4.5;
            } else if (r < 97) {
                rating = 4.0;
            } else {
                rating = 3.5;
            }

            String comment = openers[rand.nextInt(openers.length)] +
                             middles[rand.nextInt(middles.length)] +
                             closers[rand.nextInt(closers.length)];

            Avaliacao av = new Avaliacao(produto, usuario, rating, comment);
            av.setDataAvaliacao(LocalDateTime.now().minusDays(rand.nextInt(60)));
            avaliacaoRepository.save(av);
        }
    }
}