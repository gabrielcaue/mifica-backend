package com.mifica.repository;

import com.mifica.entity.DesafioGamificado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesafioRepository extends JpaRepository<DesafioGamificado, Long> {
}
