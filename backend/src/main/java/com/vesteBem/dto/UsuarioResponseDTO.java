package com.vesteBem.dto;

import com.vesteBem.model.Usuario;

public record UsuarioResponseDTO(Long id, String nome, String email, String role) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
    }
}
