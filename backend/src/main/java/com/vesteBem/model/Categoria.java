package com.vesteBem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @jakarta.persistence.Column(columnDefinition = "bytea")
    private byte[] imagem;

    @jakarta.persistence.Column(name = "tipo_imagem")
    private String tipoImagem;

    public Categoria() {}

    public Categoria(Long id) {
        this.id = id;
    }

    public Categoria(String nome) {
        this.nome = nome;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getTipoImagem() {
        return tipoImagem;
    }

    public void setTipoImagem(String tipoImagem) {
        this.tipoImagem = tipoImagem;
    }
}