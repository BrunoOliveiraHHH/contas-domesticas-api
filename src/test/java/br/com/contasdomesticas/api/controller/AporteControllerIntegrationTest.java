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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class AporteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long investimento() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Inv\",\"tipo\":\"FAMILIAR\"}");
        return criar("/api/v1/investimentos",
            "{\"nome\":\"Tesouro Selic\",\"tipoInvestimento\":\"RENDA_FIXA\",\"carteiraId\":" + carteira
                + ",\"dataAplicacao\":\"2026-01-01\"}");
    }

    private void aporte(long inv, String valor, String tipo) throws Exception {
        criar("/api/v1/investimentos/" + inv + "/aportes",
            "{\"valor\":" + valor + ",\"data\":\"2026-02-01\",\"tipo\":\"" + tipo + "\"}");
    }

    @Test
    void saldoAplicadoDeveSerAportesMenosResgates() throws Exception {
        long inv = investimento();
        aporte(inv, "1000.00", "APORTE");
        aporte(inv, "500.00", "APORTE");
        aporte(inv, "300.00", "RESGATE");

        MvcResult r = mockMvc.perform(get("/api/v1/investimentos/{id}/saldo", inv))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode saldo = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(saldo.get("saldoAplicado").decimalValue()).isEqualByComparingTo("1200.00");

        mockMvc.perform(get("/api/v1/investimentos/{id}/aportes", inv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void patrimonioDeveConsolidarOSaldo() throws Exception {
        long inv = investimento();
        aporte(inv, "2000.00", "APORTE");

        MvcResult r = mockMvc.perform(get("/api/v1/investimentos/patrimonio"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode patrimonio = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(patrimonio.get("total").decimalValue()).isGreaterThanOrEqualTo(new java.math.BigDecimal("2000.00"));
    }
}
