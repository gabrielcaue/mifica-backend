package com.mifica.repository;

import com.mifica.entity.HistoricoReputacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoReputacaoRepository extends JpaRepository<HistoricoReputacao, Long> {
    List<HistoricoReputacao> findByEmailUsuario(String email);
}
