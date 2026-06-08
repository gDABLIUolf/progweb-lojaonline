package com.vesteBem.controller;

import com.vesteBem.dto.LoginRequestDTO;
import com.vesteBem.dto.LoginResponseDTO;
import com.vesteBem.dto.RedefinirSenhaRequestDTO;
import com.vesteBem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vesteBem.dto.RecuperacaoSenhaRequestDTO;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e geração de token")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Recebe e-mail e senha, e retorna o Token JWT.")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dto) {
        try {
            String token = authService.autenticar(dto);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/esqueci-senha")
    @Operation(summary = "Solicitar recuperação de senha", description = "Gera um código de recuperação e envia para o e-mail do usuário.")
    public ResponseEntity<String> esqueciSenha(@RequestBody @Valid RecuperacaoSenhaRequestDTO dto) {
        try {
            authService.solicitarRecuperacaoDeSenha(dto);
            return ResponseEntity.ok("Se o e-mail existir em nossa base, um código de recuperação será enviado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok("Se o e-mail existir em nossa base, um código de recuperação será enviado.");
        }
    }

    @PostMapping("/redefinir-senha")
    @Operation(summary = "Redefinir a senha", description = "Valida o código de 6 dígitos e salva a nova senha do usuário.")
    public ResponseEntity<String> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequestDTO dto) {
        try {
            authService.redefinirSenha(dto);
            return ResponseEntity.ok("Senha redefinida com sucesso! Você já pode fazer login.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}