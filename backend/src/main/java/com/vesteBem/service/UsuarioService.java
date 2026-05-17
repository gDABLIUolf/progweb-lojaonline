package com.vesteBem.service;

import com.vesteBem.model.Usuario;
import com.vesteBem.repository.UsuarioRepository;
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