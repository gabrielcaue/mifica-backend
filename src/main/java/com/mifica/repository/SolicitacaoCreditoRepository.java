package com.mifica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;

public interface SolicitacaoCreditoRepository extends JpaRepository<SolicitacaoCredito, Long> {
    List<SolicitacaoCredito> findByUsuarioSolicitante(Usuario usuario);
}
