package com.vesteBem.controller.web;

import com.vesteBem.dto.LoginRequestDTO;
import com.vesteBem.dto.RecuperacaoSenhaRequestDTO;
import com.vesteBem.dto.RedefinirSenhaRequestDTO;
import com.vesteBem.model.Usuario;
import com.vesteBem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        // Se o usuário já estiver logado, redireciona para a home
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "login");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processAuth(
            @RequestParam(required = false, defaultValue = "login") String action,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String senha,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String novaSenha,
            HttpServletResponse response,
            Model model) {

        model.addAttribute("email", email);

        try {
            if ("login".equals(action)) {
                if (email == null || senha == null || email.isBlank() || senha.isBlank()) {
                    throw new IllegalArgumentException("E-mail e senha são obrigatórios.");
                }

                // Autentica o usuário e obtém o token JWT
                String token = authService.autenticar(new LoginRequestDTO(email, senha));

                // Configura o cookie JWT
                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setHttpOnly(true);
                tokenCookie.setMaxAge(7200); // 2 horas
                tokenCookie.setPath("/");
                response.addCookie(tokenCookie);

                return "redirect:/";

            } else if ("forgot".equals(action)) {
                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException("E-mail é obrigatório.");
                }

                // Solicita a recuperação de senha (envia e-mail se existir)
                try {
                    authService.solicitarRecuperacaoDeSenha(new RecuperacaoSenhaRequestDTO(email));
                } catch (Exception e) {
                    // Por segurança, fingimos sucesso mesmo que o e-mail não exista
                }

                model.addAttribute("success", "Se o e-mail estiver cadastrado, um código de 6 dígitos foi enviado.");
                model.addAttribute("activeTab", "redefinir");
                return "login";

            } else if ("redefinir".equals(action)) {
                if (email == null || codigo == null || novaSenha == null || email.isBlank() || codigo.isBlank() || novaSenha.isBlank()) {
                    throw new IllegalArgumentException("Todos os campos de redefinição são obrigatórios.");
                }

                authService.redefinirSenha(new RedefinirSenhaRequestDTO(email, codigo, novaSenha));
                model.addAttribute("success", "Senha redefinida com sucesso! Você já pode fazer login.");
                model.addAttribute("activeTab", "login");
                return "login";
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activeTab", action);
            return "login";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Limpar o cookie do token JWT
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);

        return "redirect:/login";
    }
}
