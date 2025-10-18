package com.mifica.blockchain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoBlockchainRepository extends JpaRepository<TransacaoBlockchain, Long> {
}
