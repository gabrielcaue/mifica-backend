package com.mifica.repository;

import com.mifica.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByRemetenteOrDestinatario(String remetente, String destinatario);
}
