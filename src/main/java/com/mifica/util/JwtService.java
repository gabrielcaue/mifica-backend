package com.mifica.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.mifica.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Chave secreta gerada automaticamente (pode ser persistida em config segura)
    private final Key chaveSecreta = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tempo de expiração do token: 1 hora
    private final long expiracaoMillis = 1000 * 60 * 60;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracaoMillis))
                .signWith(chaveSecreta)
                .compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
