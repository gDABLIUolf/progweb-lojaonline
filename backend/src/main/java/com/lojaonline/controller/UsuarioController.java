package com.lojaonline.controller;

import com.lojaonline.model.Usuario;
import com.lojaonline.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Usuario cadastrarUsuario(@RequestBody Usuario novoUsuario) {

        return usuarioService.salvar(novoUsuario);
    }
}