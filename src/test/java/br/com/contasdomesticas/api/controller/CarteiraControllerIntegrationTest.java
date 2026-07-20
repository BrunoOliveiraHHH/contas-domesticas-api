package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class CarteiraControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveCriarCarteiraFamiliar() throws Exception {
        mockMvc.perform(post("/api/v1/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\",\"saldoInicial\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("FAMILIAR"))
                .andExpect(jsonPath("$.moeda").value("BRL"));
    }

    @Test
    void deveCriarCarteiraIndividualComDono() throws Exception {
        long adminId = usuarioRepository.findByLogin("admin").orElseThrow().getId();

        mockMvc.perform(post("/api/v1/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Privada Admin\",\"tipo\":\"INDIVIDUAL\",\"donoId\":" + adminId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.donoLogin").value("admin"));
    }

    @Test
    void deveRejeitarIndividualSemDonoComBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Privada\",\"tipo\":\"INDIVIDUAL\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarPayloadInvalidoComBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":\"FAMILIAR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }

    @Test
    void deveRetornar404ParaCarteiraInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/carteiras/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarAtualizarERemover() throws Exception {
        MvcResult criado = mockMvc.perform(post("/api/v1/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Casa\",\"tipo\":\"FAMILIAR\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        long id = idDe(criado);

        mockMvc.perform(get("/api/v1/carteiras/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Casa"));

        mockMvc.perform(put("/api/v1/carteiras/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Casa Nova\",\"tipo\":\"FAMILIAR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Casa Nova"));

        mockMvc.perform(delete("/api/v1/carteiras/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/carteiras/{id}", id))
                .andExpect(status().isNotFound());
    }

    private long idDe(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
