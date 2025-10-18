package com.mifica.blockchain;

import com.mifica.dto.TransacaoBlockchainDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockchainService {

    @Autowired
    private TransacaoBlockchainRepository transacaoRepo;

    public TransacaoBlockchainDTO registrarTransacao(TransacaoBlockchainDTO dto) {
        TransacaoBlockchain transacao = new TransacaoBlockchain();
        transacao.setHashTransacao(dto.getHashTransacao());
        transacao.setRemetente(dto.getRemetente());
        transacao.setDestinatario(dto.getDestinatario());
        transacao.setValor(dto.getValor());
        transacao.setDataTransacao(LocalDateTime.now());

        TransacaoBlockchain salva = transacaoRepo.save(transacao);
        return toDTO(salva);
    }

    public List<TransacaoBlockchainDTO> listarTransacoes() {
        return transacaoRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TransacaoBlockchainDTO toDTO(TransacaoBlockchain transacao) {
        TransacaoBlockchainDTO dto = new TransacaoBlockchainDTO();
        dto.setId(transacao.getId());
        dto.setHashTransacao(transacao.getHashTransacao());
        dto.setRemetente(transacao.getRemetente());
        dto.setDestinatario(transacao.getDestinatario());
        dto.setValor(transacao.getValor());
        dto.setDataTransacao(transacao.getDataTransacao());
        return dto;
    }
}
