package com.mifica.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    private int reputacao = 50;

    private String nivel;

    @Enumerated(EnumType.STRING)
    private Role role; // ✅ agora usa o enum externo

    private LocalDate dataNascimento;

    private String telefone;

    @OneToMany(mappedBy = "usuarioSolicitante", cascade = CascadeType.ALL)
    private List<SolicitacaoCredito> solicitacoes = new ArrayList<>();

    @OneToMany(mappedBy = "avaliado", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoesRecebidas = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_conquistas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "conquista")
    private List<String> conquistas = new ArrayList<>();

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senha, int reputacao, String nivel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.reputacao = reputacao;
        this.nivel = nivel;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getReputacao() {
        return reputacao;
    }

    public void setReputacao(int reputacao) {
        this.reputacao = reputacao;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String novoNivel) {
        this.nivel = novoNivel;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<SolicitacaoCredito> getSolicitacoes() {
        return solicitacoes;
    }

    public void adicionarSolicitacao(SolicitacaoCredito solicitacao) {
        solicitacao.setUsuarioSolicitante(this);
        solicitacoes.add(solicitacao);
    }

    public List<Avaliacao> getAvaliacoesRecebidas() {
        return avaliacoesRecebidas;
    }

    public List<String> getConquistas() {
        return conquistas;
    }

    public void setConquistas(List<String> conquistas) {
        this.conquistas = conquistas;
    }

    // 🔥 Sistema de Níveis
    public void atualizarNivel() {
        if (this.reputacao >= 80) {
            this.nivel = "AVANCADO";
        } else if (this.reputacao >= 40) {
            this.nivel = "INTERMEDIARIO";
        } else {
            this.nivel = "INICIANTE";
        }
    }

    public boolean cumpriuMissaoHoje() {
        return solicitacoes.stream()
            .anyMatch(s -> s.getDataCriacao().toLocalDate().equals(LocalDate.now()));
    }

    public List<String> verificarConquistas() {
        List<String> conquistas = new ArrayList<>();

        if (solicitacoes.size() >= 1) {
            conquistas.add("🏆 Primeira solicitação criada");
        }

        if (reputacao >= 90) {
            conquistas.add("🏆 Reputação de Elite");
        }

        if (avaliacoesRecebidas.size() >= 5) {
            conquistas.add("🏆 Avaliado por 5 usuários diferentes");
        }

        return conquistas;
    }

    public void aplicarRecompensas() {
        if (cumpriuMissaoHoje()) {
            this.reputacao += 5;
            System.out.println("🎁 Recompensa: +5 de reputação por missão diária!");
        }

        if (solicitacoes.size() == 1) {
            this.reputacao += 10;
            System.out.println("🎁 Recompensa: Primeira solicitação criada!");
        }

        if (avaliacoesRecebidas.size() == 5) {
            this.reputacao += 15;
            System.out.println("🎁 Recompensa: Avaliado por 5 usuários!");
        }

        atualizarNivel();
    }
}
