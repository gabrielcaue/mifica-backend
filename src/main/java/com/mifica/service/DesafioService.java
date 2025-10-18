package com.mifica.service;

import com.mifica.dto.DesafioDTO;
import com.mifica.entity.DesafioGamificado;
import com.mifica.repository.DesafioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesafioService {

    @Autowired
    private DesafioRepository desafioRepository;

    public DesafioDTO criarDesafio(DesafioDTO dto) {
        DesafioGamificado desafio = new DesafioGamificado();
        desafio.setTitulo(dto.getTitulo());
        desafio.setDescricao(dto.getDescricao());
        desafio.setPontos(dto.getPontos());

        DesafioGamificado salvo = desafioRepository.save(desafio);
        dto.setId(salvo.getId());
        return dto;
    }

    public List<DesafioDTO> listarDesafios() {
        return desafioRepository.findAll().stream().map(d -> {
            DesafioDTO dto = new DesafioDTO();
            dto.setId(d.getId());
            dto.setTitulo(d.getTitulo());
            dto.setDescricao(d.getDescricao());
            dto.setPontos(d.getPontos());
            return dto;
        }).collect(Collectors.toList());
    }
}
