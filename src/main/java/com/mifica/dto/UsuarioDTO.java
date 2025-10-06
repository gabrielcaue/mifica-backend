package com.mifica.dto;

import com.mifica.entity.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @Min(value = 1, message = "Reputação mínima é 1.")
    @Max(value = 5, message = "Reputação máxima é 5.")
    private Integer reputacao;

    private String nivel;

    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nome, String email, String senha, Integer reputacao, String nivel) {
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

    public Integer getReputacao() {
        return reputacao;
    }

    public void setReputacao(Integer reputacao) {
        this.reputacao = reputacao;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
    public UsuarioDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.reputacao = usuario.getReputacao();
        this.nivel = usuario.getNivel();
    }

}
