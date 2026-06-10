package com.vesteBem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contatos")
public class Contato {

    @Id
    private Long id = 1L;

    private String telefone;
    private String email;
    private String endereco;
    private String horario;
    private String linkFacebook;
    private String linkInstagram;

    public Contato() {}

    public Contato(String telefone, String email, String endereco, String horario, String linkFacebook, String linkInstagram) {
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.horario = horario;
        this.linkFacebook = linkFacebook;
        this.linkInstagram = linkInstagram;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getLinkFacebook() { return linkFacebook; }
    public void setLinkFacebook(String linkFacebook) { this.linkFacebook = linkFacebook; }

    public String getLinkInstagram() { return linkInstagram; }
    public void setLinkInstagram(String linkInstagram) { this.linkInstagram = linkInstagram; }
}
