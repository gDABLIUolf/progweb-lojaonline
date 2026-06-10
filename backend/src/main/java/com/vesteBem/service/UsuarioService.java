package com.vesteBem.service;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.dto.UsuarioResponseDTO;
import com.vesteBem.dto.UsuarioAtualizacaoDTO;
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

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
        return new UsuarioResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));

        if (!usuario.getEmail().equalsIgnoreCase(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado por outro usuário!");
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());

        if (dto.senha() != null && !dto.senha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioSalvo);
    }
}