package com.mifica.controller;

import com.mifica.dto.DesafioDTO;
import com.mifica.service.DesafioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/desafios")
public class DesafioController {

    @Autowired
    private DesafioService desafioService;

    @PostMapping
    public ResponseEntity<DesafioDTO> criar(@RequestBody DesafioDTO dto) {
        DesafioDTO criado = desafioService.criarDesafio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<DesafioDTO>> listar() {
        return ResponseEntity.ok(desafioService.listarDesafios());
    }
}
