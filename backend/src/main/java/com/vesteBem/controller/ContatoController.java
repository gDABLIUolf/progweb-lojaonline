package com.vesteBem.controller;

import com.vesteBem.model.Contato;
import com.vesteBem.repository.ContatoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contato")
@Tag(name = "Contato", description = "Endpoints para gerenciamento das informações de contato")
public class ContatoController {

    private final ContatoRepository repository;

    public ContatoController(ContatoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Obter informações de contato")
    public ResponseEntity<Contato> obter() {
        Contato contato = repository.findById(1L).orElseGet(() -> {
            Contato padrao = new Contato(
                "(11) 99999-8888",
                "suporte@vestebem.com.br",
                "Av. Paulista, 1000 - São Paulo, SP",
                "Seg - Sex: 9h às 18h",
                "https://facebook.com",
                "https://instagram.com"
            );
            return repository.save(padrao);
        });
        return ResponseEntity.ok(contato);
    }

    @PutMapping
    @Operation(summary = "Atualizar informações de contato")
    public ResponseEntity<Contato> atualizar(@RequestBody Contato dados) {
        Contato contato = repository.findById(1L).orElseGet(() -> new Contato());
        contato.setTelefone(dados.getTelefone());
        contato.setEmail(dados.getEmail());
        contato.setEndereco(dados.getEndereco());
        contato.setHorario(dados.getHorario());
        contato.setLinkFacebook(dados.getLinkFacebook());
        contato.setLinkInstagram(dados.getLinkInstagram());
        Contato salvo = repository.save(contato);
        return ResponseEntity.ok(salvo);
    }
}
