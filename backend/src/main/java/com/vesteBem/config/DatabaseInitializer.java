package com.vesteBem.config;

import com.vesteBem.model.Categoria;
import com.vesteBem.model.Produto;
import com.vesteBem.model.Usuario;
import com.vesteBem.model.UsuarioRole;
import com.vesteBem.repository.CategoriaRepository;
import com.vesteBem.repository.ProdutoRepository;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(CategoriaRepository categoriaRepository,
                               ProdutoRepository produtoRepository,
                               UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicializar Categorias de forma robusta
        Categoria basicos = categoriaRepository.findByNome("Básicos").orElseGet(() -> {
            Categoria c = new Categoria();
            c.setNome("Básicos");
            return categoriaRepository.save(c);
        });

        Categoria malhas = categoriaRepository.findByNome("Malhas").orElseGet(() -> {
            Categoria c = new Categoria();
            c.setNome("Malhas");
            return categoriaRepository.save(c);
        });

        Categoria bottoms = categoriaRepository.findByNome("Bottoms").orElseGet(() -> {
            Categoria c = new Categoria();
            c.setNome("Bottoms");
            return categoriaRepository.save(c);
        });

        // 2. Inicializar Produtos se não existirem
        if (produtoRepository.count() == 0) {
            Produto p1 = new Produto("T-Shirt Algodão Pima Branca", 
                                     "Camiseta clássica com algodão pima peruano, caimento impecável e toque ultra macio.", 
                                     new BigDecimal("149.00"), 100, basicos);
            produtoRepository.save(p1);

            Produto p2 = new Produto("Suéter Tricot Cinza Claro", 
                                     "Suéter de tricot leve e confortável, ideal para sobreposições atemporais.", 
                                     new BigDecimal("359.00"), 50, malhas);
            produtoRepository.save(p2);

            Produto p3 = new Produto("Calça Chino Classic Areia", 
                                     "Calça chino de sarja clássica com elastano, perfeita para o dia a dia.", 
                                     new BigDecimal("289.00"), 80, bottoms);
            produtoRepository.save(p3);
        }

        // 3. Inicializar Usuários de Teste se não existirem
        if (usuarioRepository.count() == 0) {
            // Administrador
            Usuario admin = new Usuario("Administrador VesteBem", "admin@vestebem.com", passwordEncoder.encode("123456"));
            admin.setRole(UsuarioRole.ADMIN);
            usuarioRepository.save(admin);

            // Cliente Padrão
            Usuario cliente = new Usuario("João Silva", "cliente@vestebem.com", passwordEncoder.encode("123456"));
            cliente.setRole(UsuarioRole.CLIENTE);
            usuarioRepository.save(cliente);
        }
    }
}
