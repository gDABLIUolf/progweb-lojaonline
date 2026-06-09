package com.vesteBem.controller.web;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebUsuarioController {

    private final UsuarioService usuarioService;

    public WebUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public String registrarUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Executa o cadastro do usuário
            usuarioService.cadastrar(new UsuarioCadastroDTO(nome, email, senha));

            // Se for bem-sucedido, redireciona para o login com mensagem de sucesso
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça login para continuar.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("activeTab", "login");

            return "redirect:/login";

        } catch (Exception e) {
            // Se houver erro (e-mail já cadastrado, etc.), exibe no form de cadastro
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nome", nome);
            model.addAttribute("email", email);
            model.addAttribute("activeTab", "register");

            return "login";
        }
    }
}
