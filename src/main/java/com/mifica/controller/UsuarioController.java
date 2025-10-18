package com.mifica.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.mifica.dto.EstatisticasDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Usuario;
import com.mifica.service.UsuarioService;
import com.mifica.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios") // 🔧 Prefixo padronizado
public class UsuarioController {

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final String TOKEN_INVALIDO = "Token inválido ou expirado.";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/teste-swagger")
    public ResponseEntity<String> testarSwagger() {
        return ResponseEntity.ok("Swagger reconheceu o controller!");
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO criado = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO dto) {
        Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());

        if (usuario == null || !usuarioService.senhaCorreta(dto.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
        String token = jwtUtil.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(token);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.buscarPorId(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO));
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO);
            }

            UsuarioDTO dto = new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null,
                usuario.getReputacao(),
                usuario.getNivel()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO atualizado = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/perfil/reputacao")
    public ResponseEntity<String> atualizarReputacao(@RequestHeader("Authorization") String token,
                                                     @RequestBody int novaReputacao) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.atualizarReputacao(email, novaReputacao);
            return ResponseEntity.ok("Reputação atualizada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    @GetMapping("/perfil/missao-diaria")
    public ResponseEntity<String> verificarMissaoDiaria(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO);
            }

            boolean cumpriu = usuarioService.verificarMissaoDiaria(usuario);
            return ResponseEntity.ok(cumpriu
                ? "✅ Missão diária cumprida: você criou uma solicitação hoje!"
                : "❌ Missão diária pendente: crie uma solicitação para completar.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    @PostMapping("/perfil/recompensas")
    public ResponseEntity<String> aplicarRecompensas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.aplicarRecompensas(email);
            return ResponseEntity.ok("Recompensas aplicadas com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    @GetMapping("/perfil/conquistas")
    public ResponseEntity<List<String>> listarConquistas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            List<String> conquistas = usuarioService.listarConquistas(email);
            return ResponseEntity.ok(conquistas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/perfil")
    public ResponseEntity<String> deletarConta(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.deletarPorEmail(email);
            return ResponseEntity.ok("Conta excluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> verificarExistencia(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return existe ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET,POST,PUT,PATCH,DELETE,HEAD,OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @GetMapping("/estatisticas")
    public EstatisticasDTO getEstatisticas() {
        int totalUsuarios = usuarioService.contarUsuarios();
        double mediaReputacao = usuarioService.mediaReputacao();
        return new EstatisticasDTO(totalUsuarios, mediaReputacao);
    }
}
