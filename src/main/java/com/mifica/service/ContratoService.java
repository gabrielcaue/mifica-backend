package com.mifica.service;

import com.mifica.dto.ContratoDTO;
import com.mifica.entity.Contrato;
import com.mifica.repository.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    public ContratoDTO criarContrato(ContratoDTO dto) {
        Contrato contrato = new Contrato();
        contrato.setNome(dto.getNome());
        contrato.setDescricao(dto.getDescricao());
        contrato.setEnderecoBlockchain(dto.getEnderecoBlockchain());

        Contrato salvo = contratoRepository.save(contrato);

        dto.setId(salvo.getId());
        return dto;
    }

    public List<ContratoDTO> listarContratos() {
        return contratoRepository.findAll().stream().map(c -> {
            ContratoDTO dto = new ContratoDTO();
            dto.setId(c.getId());
            dto.setNome(c.getNome());
            dto.setDescricao(c.getDescricao());
            dto.setEnderecoBlockchain(c.getEnderecoBlockchain());
            return dto;
        }).collect(Collectors.toList());
    }
}
