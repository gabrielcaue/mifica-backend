package com.mifica.dto;

import java.time.LocalDateTime;

public class TransacaoBlockchainDTO {
    private Long id;
    private String hashTransacao;
    private String remetente;
    private String destinatario;
    private double valor;
    private LocalDateTime dataTransacao;

    // Getters
    public Long getId() {
        return id;
    }

    public String getHashTransacao() {
        return hashTransacao;
    }

    public String getRemetente() {
        return remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setHashTransacao(String hashTransacao) {
        this.hashTransacao = hashTransacao;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
