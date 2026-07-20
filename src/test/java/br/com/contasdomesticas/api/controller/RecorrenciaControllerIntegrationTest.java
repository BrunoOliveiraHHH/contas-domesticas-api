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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class RecorrenciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long recorrenciaDespesa() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Moradia\",\"tipo\":\"DESPESA\"}");
        return criar("/api/v1/recorrencias",
            "{\"descricao\":\"Aluguel\",\"valor\":1500.00,\"tipo\":\"DESPESA\",\"carteiraId\":" + carteira
                + ",\"categoriaId\":" + categoria + ",\"frequencia\":\"MENSAL\",\"diaVencimento\":10,"
                + "\"dataInicio\":\"2026-07-01\"}");
    }

    @Test
    void deveCriarRecorrencia() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Assinaturas\",\"tipo\":\"DESPESA\"}");

        mockMvc.perform(post("/api/v1/recorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Streaming\",\"valor\":39.90,\"tipo\":\"DESPESA\",\"carteiraId\":"
                            + carteira + ",\"categoriaId\":" + categoria
                            + ",\"frequencia\":\"MENSAL\",\"dataInicio\":\"2026-07-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.frequencia").value("MENSAL"));
    }

    @Test
    void deveGerarLancamentoDaCompetencia() throws Exception {
        long id = recorrenciaDespesa();

        mockMvc.perform(post("/api/v1/recorrencias/{id}/gerar", id).param("competencia", "2026-08-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("DESPESA"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deveSerIdempotenteAoGerarMesmaCompetencia() throws Exception {
        long id = recorrenciaDespesa();

        MvcResult r1 = mockMvc.perform(post("/api/v1/recorrencias/{id}/gerar", id).param("competencia", "2026-09-01"))
                .andExpect(status().isOk()).andReturn();
        long primeiro = objectMapper.readTree(r1.getResponse().getContentAsString()).get("id").asLong();

        MvcResult r2 = mockMvc.perform(post("/api/v1/recorrencias/{id}/gerar", id).param("competencia", "2026-09-01"))
                .andExpect(status().isOk()).andReturn();
        long segundo = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();

        assertThat(segundo).isEqualTo(primeiro);
    }

    @Test
    void deveRetornar404ParaRecorrenciaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/recorrencias/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}
