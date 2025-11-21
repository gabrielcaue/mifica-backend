package com.mifica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mifica.dto.LoginRequestDTO;
import com.mifica.entity.Usuario;
import com.mifica.service.UsuarioService;
import com.mifica.util.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());

        if (usuario == null || !passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // Gera o token JWT
        String token = jwtService.gerarToken(usuario);

        // Monta a resposta JSON com token + dados do usuário
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());
        resposta.put("role", usuario.getRole());
        resposta.put("reputacao", usuario.getReputacao());
        resposta.put("conquistas", usuario.getConquistas());

        return ResponseEntity.ok(resposta);
    }
}
