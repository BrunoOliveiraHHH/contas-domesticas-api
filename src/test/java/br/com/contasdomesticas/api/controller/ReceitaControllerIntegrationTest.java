package br.com.contasdomesticas.api.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ReceitaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long carteira() throws Exception {
        return criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
    }

    private long categoria(String tipo) throws Exception {
        return criar("/api/v1/categorias", "{\"nome\":\"Cat " + tipo + "\",\"tipo\":\"" + tipo + "\"}");
    }

    @Test
    void deveCriarReceita() throws Exception {
        long c = carteira();
        long cat = categoria("RECEITA");

        mockMvc.perform(post("/api/v1/receitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Salario\",\"valor\":5000.00,\"dataCompetencia\":\"2026-07-05\","
                            + "\"carteiraId\":" + c + ",\"categoriaId\":" + cat + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("RECEITA"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deveRejeitarCategoriaDeTipoIncompativel() throws Exception {
        long c = carteira();
        long catDespesa = categoria("DESPESA");

        mockMvc.perform(post("/api/v1/receitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Errada\",\"valor\":10.00,\"dataCompetencia\":\"2026-07-05\","
                            + "\"carteiraId\":" + c + ",\"categoriaId\":" + catDespesa + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarValorInvalido() throws Exception {
        long c = carteira();
        long cat = categoria("RECEITA");

        mockMvc.perform(post("/api/v1/receitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Zero\",\"valor\":0,\"dataCompetencia\":\"2026-07-05\","
                            + "\"carteiraId\":" + c + ",\"categoriaId\":" + cat + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }

    @Test
    void deveRetornar404ParaReceitaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/receitas/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}
