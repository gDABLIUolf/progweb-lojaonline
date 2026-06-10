package com.vesteBem.service;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.dto.UsuarioResponseDTO;
import com.vesteBem.dto.UsuarioAtualizacaoDTO;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.UsuarioRepository;
import com.vesteBem.model.UsuarioRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
        if (dto.role() != null && !dto.role().trim().isEmpty()) {
            novoUsuario.setRole(UsuarioRole.valueOf(dto.role().toUpperCase()));
        }

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

        if (dto.role() != null && !dto.role().trim().isEmpty()) {
            usuario.setRole(UsuarioRole.valueOf(dto.role().toUpperCase()));
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioSalvo);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
        usuarioRepository.delete(usuario);
    }
}