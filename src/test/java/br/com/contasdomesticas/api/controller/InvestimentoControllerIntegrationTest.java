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
class InvestimentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long carteira() throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/carteiras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Investimentos\",\"tipo\":\"FAMILIAR\"}"))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void deveCriarInvestimento() throws Exception {
        long c = carteira();
        mockMvc.perform(post("/api/v1/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"CDB Banco X\",\"tipoInvestimento\":\"RENDA_FIXA\",\"indexador\":\"CDI\","
                            + "\"carteiraId\":" + c + ",\"dataAplicacao\":\"2026-01-10\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoInvestimento").value("RENDA_FIXA"))
                .andExpect(jsonPath("$.indexador").value("CDI"));
    }

    @Test
    void deveRetornar404ParaInvestimentoInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/investimentos/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        long c = carteira();
        mockMvc.perform(post("/api/v1/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipoInvestimento\":\"POUPANCA\",\"carteiraId\":" + c
                            + ",\"dataAplicacao\":\"2026-01-10\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
