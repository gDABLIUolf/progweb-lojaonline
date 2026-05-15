package com.lojaonline.service;

import com.lojaonline.model.Usuario;
import com.lojaonline.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}