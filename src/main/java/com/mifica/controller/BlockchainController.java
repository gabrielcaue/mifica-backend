package com.mifica.controller;

import com.mifica.dto.TransacaoBlockchainDTO;
import com.mifica.blockchain.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @PostMapping("/transacoes")
    public ResponseEntity<TransacaoBlockchainDTO> registrar(@RequestBody TransacaoBlockchainDTO dto) {
        TransacaoBlockchainDTO registrada = blockchainService.registrarTransacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
    }

    @GetMapping("/transacoes")
    public ResponseEntity<List<TransacaoBlockchainDTO>> listar() {
        return ResponseEntity.ok(blockchainService.listarTransacoes());
    }
}
