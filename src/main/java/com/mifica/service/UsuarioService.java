package com.mifica.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifica.dto.UsuarioCadastroDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean emailJaExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public UsuarioDTO criar(UsuarioDTO dto) {
        String senhaCriptografada = criptografarSenha(dto.getSenha());
        return criarUsuario(dto, senhaCriptografada);
    }

    private UsuarioDTO criarUsuario(UsuarioDTO dto, String senhaCriptografada) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(senhaCriptografada);
        usuario.setReputacao(dto.getReputacao() != null ? dto.getReputacao() : 1);
        usuario.setNivel(dto.getNivel());

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);
    }

    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::converterParaDTO);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public UsuarioDTO buscarPorEmailDTO(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? converterParaDTO(usuario) : null;
    }

    public void atualizarPerfil(String email, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        if (dadosAtualizados.getNome() != null) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getSenha() != null) {
            usuario.setSenha(criptografarSenha(dadosAtualizados.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    public boolean atualizarReputacao(String email, int novaReputacao) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return false;

        usuario.setReputacao(novaReputacao);
        usuarioRepository.save(usuario);
        return true;
    }

    public void atualizarReputacao(Usuario usuario, int novaReputacao) {
        usuario.setReputacao(novaReputacao);
        usuario.atualizarNivel();
        usuarioRepository.save(usuario);
    }

    public Usuario criarSolicitacao(BigDecimal valor, String descricao, LocalDate prazo, String email) {
        Usuario usuario = buscarPorEmail(email);

        SolicitacaoCredito nova = new SolicitacaoCredito();
        nova.setValorSolicitado(valor);
        nova.setDescricao(descricao);
        nova.setPrazoPagamento(prazo);
        nova.setStatus("PENDENTE");
        nova.setDataSolicitacao(LocalDateTime.now());

        usuario.adicionarSolicitacao(nova);
        return usuarioRepository.save(usuario);
    }

    public List<SolicitacaoCredito> listarSolicitacoes(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? usuario.getSolicitacoes() : List.of();
    }

    public boolean verificarMissaoDiaria(Usuario usuario) {
        return usuario.cumpriuMissaoHoje();
    }

    public List<String> listarConquistas(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado.");
        return usuario.getConquistas();
    }

    public void aplicarRecompensas(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        if (usuario.getConquistas() == null) {
            usuario.setConquistas(new ArrayList<>());
        }

        if (!usuario.getConquistas().contains("Primeira solicitação") && usuario.getSolicitacoes().size() >= 1) {
            usuario.getConquistas().add("Primeira solicitação");
        }

        if (usuario.getReputacao() >= 5 && !usuario.getConquistas().contains("Reputação 5+")) {
            usuario.getConquistas().add("Reputação 5+");
        }

        usuarioRepository.save(usuario);
    }

    public void aplicarRecompensasCertas(Usuario usuario) {
        boolean cumpriuMissao = verificarMissaoDiaria(usuario);
        if (cumpriuMissao) {
            usuario.setReputacao(usuario.getReputacao() + 1);
        }

        if (usuario.getConquistas() == null) {
            usuario.setConquistas(new ArrayList<>());
        }

        if (usuario.getSolicitacoes().size() == 1 && !usuario.getConquistas().contains("Primeira solicitação")) {
            usuario.getConquistas().add("Primeira solicitação");
        }

        if (usuario.getReputacao() >= 10 && !usuario.getConquistas().contains("Reputação 10+")) {
            usuario.getConquistas().add("Reputação 10+");
        }

        if (usuario.getReputacao() >= 20) {
            usuario.setNivel("Expert");
        } else if (usuario.getReputacao() >= 10) {
            usuario.setNivel("Intermediário");
        } else {
            usuario.setNivel("Iniciante");
        }

        usuarioRepository.save(usuario);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void excluir(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    public void deletarPorEmail(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado para exclusão.");
        usuarioRepository.delete(usuario);
    }

    public boolean senhaCorreta(String senhaDigitada, String senhaArmazenada) {
        return passwordEncoder.matches(senhaDigitada, senhaArmazenada);
    }

    private String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            null,
            usuario.getReputacao(),
            usuario.getNivel(),
            usuario.getDataNascimento(),
            usuario.getTelefone(),
            usuario.getRole()
        );
    }

    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(this::converterParaDTO).toList();
    }

    public Optional<UsuarioDTO> atualizar(Long id, UsuarioDTO dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) return Optional.empty();

        Usuario usuario = optionalUsuario.get();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(criptografarSenha(dto.getSenha()));
        }

        if (dto.getReputacao() != null) {
            usuario.setReputacao(dto.getReputacao());
        }

        usuarioRepository.save(usuario);
        return Optional.of(converterParaDTO(usuario));
    }

    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(criptografarSenha(dto.getSenha()));
        }

        usuario.setReputacao(dto.getReputacao());
        usuario.setNivel(dto.getNivel());

        Usuario atualizado = usuarioRepository.save(usuario);
        return converterParaDTO(atualizado);
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public int contarUsuarios() {
        return Math.toIntExact(usuarioRepository.count());
    }

    public double mediaReputacao() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) return 0.0;

        double soma = usuarios.stream()
                              .mapToDouble(Usuario::getReputacao)
                              .sum();

        return soma / usuarios.size();
    }

    public void cadastrarNovoUsuario(UsuarioCadastroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setTelefone(dto.getTelefone());

        // 🔐 Define o papel conforme o DTO (com fallback para ROLE_USER)
        usuario.setRole(formatarPapel(dto.getRole()));

        usuarioRepository.save(usuario);
    }

    public void alterarPapel(Long id, String novoPapel) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setRole(formatarPapel(novoPapel));
        usuarioRepository.save(usuario);
    }
    
    private String formatarPapel(String papel) {
        if (papel == null || papel.isBlank()) {
            return "ROLE_USER";
        }
        return papel.startsWith("ROLE_") ? papel : "ROLE_" + papel.toUpperCase();
    }

    
}