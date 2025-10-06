package com.mifica.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 🔹 Verifica se o e-mail já está cadastrado
    public boolean emailJaExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // 🔹 Criação de usuário com senha criptografada
    public UsuarioDTO criar(UsuarioDTO dto) {
        String senhaCriptografada = criptografarSenha(dto.getSenha());
        return criarUsuario(dto, senhaCriptografada);
    }

    private UsuarioDTO criarUsuario(UsuarioDTO dto, String senhaCriptografada) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(senhaCriptografada);
        usuario.setReputacao(dto.getReputacao());
        usuario.setNivel(dto.getNivel());
        if (dto.getReputacao() == null) {
            dto.setReputacao(1);
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);

    }

    // 🔹 Buscar por ID e retornar DTO
    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::converterParaDTO);
    }

    // 🔹 Buscar por e-mail (entidade)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // 🔹 Buscar por e-mail (DTO)
    public UsuarioDTO buscarPorEmailDTO(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? converterParaDTO(usuario) : null;
    }

    // 🔹 Atualizar dados básicos do perfil
    public void atualizarPerfil(String email, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        if (dadosAtualizados.getNome() != null) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getSenha() != null) {
            usuario.setSenha(dadosAtualizados.getSenha());
        }

        usuarioRepository.save(usuario);
    }

    // 🔹 Atualizar reputação simples
    public boolean atualizarReputacao(String email, int novaReputacao) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return false;

        usuario.setReputacao(novaReputacao);
        usuarioRepository.save(usuario);
		return false;
    }

    // 🔹 Atualizar reputação com lógica de nível
    public void atualizarReputacao(Usuario usuario, int novaReputacao) {
        usuario.setReputacao(novaReputacao);
        usuario.atualizarNivel();
        usuarioRepository.save(usuario);
    }

    // 🔹 Criar solicitação de crédito
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

    // 🔹 Listar solicitações do usuário
    public List<SolicitacaoCredito> listarSolicitacoes(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? usuario.getSolicitacoes() : List.of();
    }

    // 🔹 Verificar missão diária
    public boolean verificarMissaoDiaria(Usuario usuario) {
        return usuario.cumpriuMissaoHoje();
    }

    // 🔹 Listar conquistas desbloqueadas
    public List<String> listarConquistas(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = optionalUsuario.get();
        return usuario.getConquistas();
    }

    // 🔹 Aplicar recompensas gamificadas
    public void aplicarRecompensasAgora(Usuario usuario) {
        usuario.aplicarRecompensas();
        usuarioRepository.save(usuario);
    }

    // 🔹 Utilitários
    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void excluir(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    public boolean senhaCorreta(String senhaDigitada, String senhaArmazenada) {
        return new BCryptPasswordEncoder().matches(senhaDigitada, senhaArmazenada);
    }

    private String criptografarSenha(String senha) {
        return new BCryptPasswordEncoder().encode(senha);
    }

    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            null, // senha omitida
            usuario.getReputacao(),
            usuario.getNivel()
        );
    }

    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
            .map(usuario -> new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null, // senha omitida por segurança
                usuario.getReputacao(),
                usuario.getNivel()
            ))
            .toList();
    }
    public Optional<UsuarioDTO> atualizar(Long id, UsuarioDTO dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = optionalUsuario.get();

        // Atualiza os campos permitidos
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        // Atualiza a senha apenas se vier preenchida
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(dto.getSenha());
        }

        // Atualiza reputação e nível se vierem preenchidos
        if (dto.getReputacao() != null) {
            usuario.setReputacao(dto.getReputacao());
        }
		return null;

    }
    public void aplicarRecompensasCertas(Usuario usuario) {
        // Recompensa por missão diária
        boolean cumpriuMissao = verificarMissaoDiaria(usuario);
        if (cumpriuMissao) {
            usuario.setReputacao(usuario.getReputacao() + 1);
        }

        // Recompensa por reputação acumulada
        if (usuario.getReputacao() >= 10 && !usuario.getConquistas().contains("Reputação 10+")) {
            usuario.getConquistas().add("Reputação 10+");
        }

        // Recompensa por primeira solicitação
        if (usuario.getSolicitacoes().size() == 1 && !usuario.getConquistas().contains("Primeira solicitação")) {
            usuario.getConquistas().add("Primeira solicitação");
        }

        // Atualiza nível com base na reputação
        if (usuario.getReputacao() >= 20) {
            usuario.setNivel("Expert");
        } else if (usuario.getReputacao() >= 10) {
            usuario.setNivel("Intermediário");
        } else {
            usuario.setNivel("Iniciante");
        }

        // Salva alterações
        usuarioRepository.save(usuario);
    }

    public void aplicarRecompensas(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        List<String> conquistas = usuario.getConquistas();

        if (!conquistas.contains("Primeira solicitação")) {
            conquistas.add("Primeira solicitação");
        }

        if (usuario.getReputacao() >= 5 && !conquistas.contains("Reputação 5+")) {
            conquistas.add("Reputação 5+");
        }

        usuarioRepository.save(usuario);
    }


    public void aplicarRecompensas(Usuario usuario) {
        // Aqui você já tem o usuário, não precisa buscar de novo
        // Pode aplicar recompensas diretamente

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

    public void deletarPorEmail(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado para exclusão.");
        }

        Usuario usuario = optionalUsuario.get();
        usuarioRepository.delete(usuario);
    }
    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }
   
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
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



    
}
