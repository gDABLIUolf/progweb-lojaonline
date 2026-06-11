package com.vesteBem.service;

import com.vesteBem.model.Newsletter;
import com.vesteBem.repository.NewsletterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {

    @Autowired
    private NewsletterRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    public String inscrever(String email) {
        if (repository.existsByEmail(email)) {
            return "Este e-mail já está inscrito em nossa newsletter!";
        }

        Newsletter novaInscricao = new Newsletter();
        novaInscricao.setEmail(email);
        repository.save(novaInscricao);

        enviarEmailBoasVindas(email);

        return "Inscrição realizada com sucesso!";
    }

    private void enviarEmailBoasVindas(String destinatario) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("Bem-vindo à VesteBem! 🎉");
        mensagem.setText("Olá!\n\nObrigado por se inscrever na newsletter da VesteBem. " +
                "A partir de agora, você receberá nossas novidades e ofertas exclusivas em primeira mão.\n\n" +
                "Fique de olho!\n\nEquipe VesteBem.");

        mailSender.send(mensagem);
    }
}