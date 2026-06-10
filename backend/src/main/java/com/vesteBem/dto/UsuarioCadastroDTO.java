package com.vesteBem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroDTO(

        @Schema(description = "Nome completo do cliente", example = "João da Silva")
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "E-mail válido que será usado para login", example = "joao.silva@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @Schema(description = "Senha de acesso (será criptografada no banco)", example = "SenhaForte123!")
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        @Schema(description = "Papel do usuário (ADMIN ou CLIENTE)", example = "CLIENTE")
        String role
) {}