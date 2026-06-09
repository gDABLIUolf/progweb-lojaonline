package com.vesteBem.controller.web;

import com.vesteBem.model.Usuario;
import com.vesteBem.repository.ProdutoRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebHomeController {

    private final ProdutoRepository produtoRepository;

    public WebHomeController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Obter usuário logado, se houver
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                model.addAttribute("user", principal);
            }
        }

        // Listar produtos para a vitrine
        model.addAttribute("produtos", produtoRepository.findAll());

        return "index";
    }
}
