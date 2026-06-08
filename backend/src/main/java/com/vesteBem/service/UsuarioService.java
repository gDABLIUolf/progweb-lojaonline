package com.vesteBem.service;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String cadastrar(UsuarioCadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado!");
        }
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), senhaCriptografada);

        usuarioRepository.save(novoUsuario);

        return "Usuário cadastrado com sucesso com o ID: " + novoUsuario.getId();
    }
}