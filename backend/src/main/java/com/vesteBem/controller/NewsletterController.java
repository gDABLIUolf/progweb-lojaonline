package com.vesteBem.controller;

import com.vesteBem.service.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    @Autowired
    private NewsletterService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> inscrever(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String mensagem = service.inscrever(email);

        return ResponseEntity.ok(Map.of("mensagem", mensagem));
    }
}