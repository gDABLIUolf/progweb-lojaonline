package com.vesteBem.service;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String cadastrar(UsuarioCadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado!");
        }
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), dto.senha());

        usuarioRepository.save(novoUsuario);

        return "Usuário cadastrado com sucesso com o ID: " + novoUsuario.getId();
    }
}