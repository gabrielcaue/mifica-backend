package com.mifica.entity;

import jakarta.persistence.*;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private String enderecoBlockchain;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getEnderecoBlockchain() { return enderecoBlockchain; }
    public void setEnderecoBlockchain(String enderecoBlockchain) { this.enderecoBlockchain = enderecoBlockchain; }
}
