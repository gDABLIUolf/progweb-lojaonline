package com.vesteBem.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.vesteBem.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret:meu-segredo-super-secreto-local}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String permissaoDoUsuario = usuario.getRole().name();

            return JWT.create()
                    .withIssuer("VesteBem API")
                    .withSubject(usuario.getEmail())
                    .withClaim("role", permissaoDoUsuario)
                    .withClaim("usuarioId", usuario.getId())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-04:00"));
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("VesteBem API")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception exception) {
            return "";
        }
    }
}