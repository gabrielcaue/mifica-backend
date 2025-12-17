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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mifica.dto.EstatisticasDTO;
import com.mifica.dto.LoginDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Role;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import com.mifica.service.UsuarioService;
import com.mifica.util.JwtUtil;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final String TOKEN_INVALIDO = "Token inválido ou expirado.";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.cadastro.senha}")
    private String senhaCadastroAdmin;

    // 🔧 Teste Swagger
    @GetMapping("/teste-swagger")
    public ResponseEntity<String> testarSwagger() {
        return ResponseEntity.ok("Swagger reconheceu o controller!");
    }

    // 🔧 Cadastro de usuário comum
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto) {
        if (usuarioService.emailJaExiste(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }
        UsuarioDTO novo = usuarioService.criar(dto);
        return ResponseEntity.ok(novo);
    }

    // 🔧 Cadastro de administrador
    @PostMapping("/cadastro-admin")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody Map<String, Object> payload) {
        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome((String) payload.get("nome"));
        novoAdmin.setEmail((String) payload.get("email"));
        novoAdmin.setSenha(passwordEncoder.encode((String) payload.get("senha")));
        novoAdmin.setRole(Role.ROLE_ADMIN);
        novoAdmin.setReputacao(100);
        novoAdmin.setConquistas(new ArrayList<>());
        novoAdmin.setTelefone((String) payload.get("telefone"));

        String dataStr = (String) payload.get("dataNascimento");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        novoAdmin.setDataNascimento(LocalDate.parse(dataStr, formatter));

        usuarioRepository.save(novoAdmin);
        return ResponseEntity.ok("Administrador cadastrado com sucesso");
    }

 // 🔧 Login com JWT via POST (frontend/app)
    @PostMapping("/login")
    public ResponseEntity<?> loginPost(@RequestBody LoginDTO dto) {
        boolean valido = usuarioService.validarLogin(dto.getEmail(), dto.getSenha());
        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());
        String token = jwtUtil.gerarToken(usuario.getEmail());

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("reputacao", usuario.getReputacao());
        resposta.put("conquistas", usuario.getConquistas());

        return ResponseEntity.ok(resposta);
    }

    // 🔧 Listar todos (ADMIN)
    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // 🔧 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.buscarPorId(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO));
    }

    // 🔧 Perfil do usuário autenticado
    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO);
            }
            UsuarioDTO dto = usuarioService.converterParaDTO(usuario);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO atualizado = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    // 🔧 Atualizar reputação
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

    // 🔧 Missão diária
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
                ? "✅ Missão diária cumprida!"
                : "❌ Missão diária pendente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Recompensas
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

    // 🔧 Conquistas
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

    // 🔧 Deletar conta
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

    // 🔧 HEAD para verificar existência
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> verificarExistencia(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return existe ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // 🔧 OPTIONS
    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET,POST,PUT,PATCH,DELETE,HEAD,OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    // 🔧 Estatísticas
    @GetMapping("/estatisticas")
    public EstatisticasDTO getEstatisticas() {
        int totalUsuarios = usuarioService.contarUsuarios();
        double mediaReputacao = usuarioService.mediaReputacao();
        return new EstatisticasDTO(totalUsuarios, mediaReputacao);
    }

@PutMapping("/{id}/senha")
public ResponseEntity<?> atualizarSenha(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload,
        @RequestHeader("Authorization") String token) {
    try {
        String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null || !usuario.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não autorizado.");
        }

        String senhaAtual = payload.get("senhaAtual");
        String senhaNova = payload.get("senhaNova");

        usuarioService.atualizarSenha(id, senhaAtual, senhaNova);

        return ResponseEntity.ok("Senha atualizada com sucesso!");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}



}