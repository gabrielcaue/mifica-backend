package com.mifica.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.mifica.dto.LoginDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Usuario;
import com.mifica.service.UsuarioService;
import com.mifica.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final String TOKEN_INVALIDO = "Token inválido ou expirado.";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 Teste de conexão
    @GetMapping("/teste-swagger")
    public ResponseEntity<String> testarSwagger() {
        return ResponseEntity.ok("Swagger reconheceu o controller!");
    }

    // 🔹 Criar novo usuário
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO criado = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    // 🔐 Login e geração de token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO dto) {
        Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());

        if (usuario == null || !usuarioService.senhaCorreta(dto.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
        String token = jwtUtil.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(token);
    }

    // 🔹 Buscar todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // 🔹 Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.buscarPorId(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO));
    }

    // 🔹 Perfil do usuário autenticado
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

    // 🔹 Atualizar dados do usuário (PUT = substituição completa)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO atualizado = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }



    // 🔹 Atualizar reputação (PATCH = alteração parcial)
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

    // 🔹 Verificar missão diária
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

    // 🔹 Aplicar recompensas
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

    // 🔹 Ver conquistas desbloqueadas
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




    // 🔹 Deletar conta do usuário autenticado
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

    // 🔹 HEAD: verificar se usuário existe
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> verificarExistencia(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return existe ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // 🔹 OPTIONS: informar métodos disponíveis
    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET,POST,PUT,PATCH,DELETE,HEAD,OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
