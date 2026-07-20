package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.repository.AuditoriaRepository;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AuditoriaRepository auditoriaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String json(String login, String nome, String senha) {
        return """
                {"login":"%s","nomeExibicao":"%s","senha":"%s"}
                """.formatted(login, nome, senha);
    }

    @Test
    void seedDeTesteDeveConterUsuarioAdminComSenhaBcrypt() {
        Usuario admin = usuarioRepository.findByLogin("admin").orElseThrow();
        assertThat(admin.getNomeExibicao()).isEqualTo("Admin");
        assertThat(passwordEncoder.matches("admin", admin.getSenha())).isTrue();
    }

    @Test
    void deveCriarUsuarioSemExporSenha() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("joao", "Joao Silva", "senha123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("joao"))
                .andExpect(jsonPath("$.criadoEm").exists())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRejeitarLoginDuplicadoComConflito() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("maria", "Maria Souza", "senha123")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("maria", "Maria Souza", "senha123")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarPayloadInvalidoComBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("", "Sem Login", "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }

    @Test
    void deveBuscarAtualizarERemover() throws Exception {
        MvcResult criado = mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("carlos", "Carlos Lima", "senha123")))
                .andExpect(status().isCreated())
                .andReturn();
        long id = idDe(criado);

        mockMvc.perform(get("/api/v1/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("carlos"));

        mockMvc.perform(put("/api/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("carlos", "Carlos Atualizado", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeExibicao").value("Carlos Atualizado"));

        mockMvc.perform(delete("/api/v1/usuarios/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/usuarios/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Usuário não encontrado com o id: 999999"));
    }

    @Test
    void deveRegistrarAuditoriaDaRequisicao() throws Exception {
        long antes = auditoriaRepository.count();

        mockMvc.perform(get("/api/v1/usuarios")).andExpect(status().isOk());

        assertThat(auditoriaRepository.count()).isGreaterThan(antes);
        assertThat(auditoriaRepository.findAll())
                .anyMatch(a -> "/api/v1/usuarios".equals(a.getEndpoint())
                        && "GET".equals(a.getMetodoHttp())
                        && a.getStatusResposta() != null);
    }

    private long idDe(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
