package com.vesteBem.service;

import com.vesteBem.dto.LoginRequestDTO;
import com.vesteBem.model.Usuario;
import com.vesteBem.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vesteBem.dto.RecuperacaoSenhaRequestDTO;
import java.time.LocalDateTime;
import java.util.Random;
import com.vesteBem.dto.RedefinirSenhaRequestDTO;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    public AuthService(UsuarioRepository repository, PasswordEncoder passwordEncoder,
                       TokenService tokenService, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    public String autenticar(LoginRequestDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos."));

        boolean senhaValida = passwordEncoder.matches(dto.senha(), usuario.getSenha());

        if (!senhaValida) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        return tokenService.gerarToken(usuario);
    }

    public void solicitarRecuperacaoDeSenha(RecuperacaoSenhaRequestDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado."));

        String codigo = String.format("%06d", new Random().nextInt(999999));

        usuario.setCodigoRecuperacao(codigo);
        usuario.setDataExpiracaoCodigo(LocalDateTime.now().plusMinutes(15));

        repository.save(usuario);

        String assunto = "Recuperação de Senha - VesteBem";
        String mensagem = "Olá " + usuario.getNome() + ",\n\n"
                + "Recebemos um pedido para redefinir a sua senha.\n"
                + "O seu código de recuperação é: " + codigo + "\n\n"
                + "Este código é válido por 15 minutos.\n"
                + "Se você não solicitou esta alteração, ignore este e-mail.";

        emailService.enviarEmailTexto(usuario.getEmail(), assunto, mensagem);
    }

    public void redefinirSenha(RedefinirSenhaRequestDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado ou código inválido."));

        if (usuario.getCodigoRecuperacao() == null || !usuario.getCodigoRecuperacao().equals(dto.codigo())) {
            throw new IllegalArgumentException("Código de recuperação inválido.");
        }

        if (usuario.getDataExpiracaoCodigo() == null || usuario.getDataExpiracaoCodigo().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("O código de recuperação expirou. Solicite um novo.");
        }

        String novaSenhaCriptografada = passwordEncoder.encode(dto.novaSenha());
        usuario.setSenha(novaSenhaCriptografada);

        usuario.setCodigoRecuperacao(null);
        usuario.setDataExpiracaoCodigo(null);

        repository.save(usuario);
    }
}