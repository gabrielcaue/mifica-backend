package com.mifica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UsuarioCadastroDTO {

    @NotBlank(message = "Nome completo é obrigatório.")
    private String nome;

    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @NotNull(message = "Data de nascimento é obrigatória.")
    private LocalDate dataNascimento;

    @NotBlank(message = "Número de telefone é obrigatório.")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos.")
    private String telefone;

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getRole() { return role; }
    public void setRole(String role) {this.role = role; }
    
    @NotBlank(message = "Papel do usuário é obrigatório.")
    private String role;

}
