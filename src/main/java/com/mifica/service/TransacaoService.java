package com.mifica.service;

import com.mifica.entity.Transacao;
import com.mifica.repository.TransacaoRepository;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Transacao> listarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("ID do usuário não encontrado"));
        
        return transacaoRepository.findByRemetenteOrDestinatario(email, email);
    }

    public void criarTransacao(String email, Transacao transacao) {
        transacao.setRemetente(email);
        transacao.setDataTransacao(java.time.LocalDateTime.now());
        transacaoRepository.save(transacao);
    }

    public void deletarPorId(Long id) {
        transacaoRepository.deleteById(id);
    }
    
}



