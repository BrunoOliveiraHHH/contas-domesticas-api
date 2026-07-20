package br.com.contasdomesticas.api.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criarRaiz(String nome, String tipo) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\",\"tipo\":\"" + tipo + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void deveCriarCategoriaRaiz() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Moradia\",\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("DESPESA"));
    }

    @Test
    void subcategoriaDeveHerdarTipoDaPai() throws Exception {
        long paiId = criarRaiz("Moradia", "DESPESA");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Aluguel\",\"categoriaPaiId\":" + paiId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("DESPESA"))
                .andExpect(jsonPath("$.categoriaPaiId").value(paiId));
    }

    @Test
    void deveRejeitarPaiInexistente() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Orfa\",\"categoriaPaiId\":999999}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarRaizSemTipo() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"SemTipo\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarCicloNaAtualizacao() throws Exception {
        long id = criarRaiz("Categoria", "RECEITA");

        mockMvc.perform(put("/api/v1/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Categoria\",\"categoriaPaiId\":" + id + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404ParaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/categorias/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
