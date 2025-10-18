package com.mifica.blockchain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransacaoBlockchain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashTransacao;
    private String remetente;
    private String destinatario;
    private double valor;
    private LocalDateTime dataTransacao;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHashTransacao() { return hashTransacao; }
    public void setHashTransacao(String hashTransacao) { this.hashTransacao = hashTransacao; }

    public String getRemetente() { return remetente; }
    public void setRemetente(String remetente) { this.remetente = remetente; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }
}
