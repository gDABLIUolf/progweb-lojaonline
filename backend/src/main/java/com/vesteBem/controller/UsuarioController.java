package com.vesteBem.controller;

import com.vesteBem.dto.UsuarioCadastroDTO;
import com.vesteBem.dto.UsuarioResponseDTO;
import com.vesteBem.dto.UsuarioAtualizacaoDTO;
import com.vesteBem.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de clientes e administradores")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário", description = "Rota para criar um novo usuário na plataforma VesteBem.")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody @Valid UsuarioCadastroDTO dto) {
        try {
            String resultado = usuarioService.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar detalhes de um usuário", description = "Recupera os detalhes públicos de um perfil de usuário por ID.")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de um usuário", description = "Atualiza nome, e-mail e opcionalmente a senha de um usuário.")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioAtualizacaoDTO dto) {
        try {
            UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizar(id, dto);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}