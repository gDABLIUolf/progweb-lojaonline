package com.vesteBem.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable=false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable=false)
    private String status = "PENDENTE";

    @Column(name = "metodo_pagamento")
    private String metodoPagamento;

    @Column(name = "valor_total_pago")
    private java.math.BigDecimal valorTotalPago;

    public Pedido() {}

    public Pedido(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public java.math.BigDecimal getValorTotalPago() {
        return valorTotalPago;
    }

    public void setValorTotalPago(java.math.BigDecimal valorTotalPago) {
        this.valorTotalPago = valorTotalPago;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
