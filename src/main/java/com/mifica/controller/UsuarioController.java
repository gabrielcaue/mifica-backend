package com.mifica.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.mifica.dto.EstatisticasDTO;
import com.mifica.dto.LoginDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
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
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());

        if (usuario == null || !usuarioService.senhaCorreta(dto.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        String token = jwtUtil.gerarToken(usuario.getEmail());

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId()); // ← ESSENCIAL
        resposta.put("nome", usuario.getNome());
        resposta.put("reputacao", usuario.getReputacao());
        resposta.put("conquistas", usuario.getConquistas());

        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
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
            	    null, // senha omitida
            	    usuario.getReputacao(),
            	    usuario.getNivel(),
            	    usuario.getDataNascimento(),
            	    usuario.getTelefone(),
            	    usuario.getRole()
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
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.cadastro.senha}")
    private String senhaCadastroAdmin;

    @PostMapping("/cadastro-admin")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody Map<String, Object> payload) {
        String senhaAcesso = (String) payload.get("senhaAcesso");

        if (senhaAcesso == null || !senhaAcesso.equals(senhaCadastroAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: senha inválida.");
        }

        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome((String) payload.get("nome"));
        novoAdmin.setEmail((String) payload.get("email"));
        novoAdmin.setSenha(passwordEncoder.encode((String) payload.get("senha")));
        novoAdmin.setRole("ROLE_ADMIN");
        novoAdmin.setReputacao(100);
        novoAdmin.setConquistas(new ArrayList<>());
        novoAdmin.setTelefone((String) payload.get("telefone"));

        String dataStr = (String) payload.get("dataNascimento");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        novoAdmin.setDataNascimento(LocalDate.parse(dataStr, formatter));

        usuarioRepository.save(novoAdmin);
        return ResponseEntity.ok("Administrador cadastrado com sucesso");
    }



    
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setRole("USER"); // ← perfil padrão
        novoUsuario.setReputacao(0);
        novoUsuario.setConquistas(new ArrayList<>());

        usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso");
    }



    @PutMapping("/admin/promover/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> promover(@PathVariable Long id) {
        usuarioService.alterarPapel(id, "ROLE_ADMIN");
        return ResponseEntity.ok("Usuário promovido para ADMIN.");
    }
    
    @PutMapping("/admin/rebaixar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rebaixar(@PathVariable Long id) {
        usuarioService.alterarPapel(id, "ROLE_USER");
        return ResponseEntity.ok("Usuário rebaixado para USER.");
    }

    @RestController
    @RequestMapping("/api/config")
    public class ConfigController {

        @Value("${app.security.dev-mode}")
        private boolean devMode;

        @GetMapping("/dev-mode")
        public boolean isDevMode() {
            return devMode;
        }
    }
}
