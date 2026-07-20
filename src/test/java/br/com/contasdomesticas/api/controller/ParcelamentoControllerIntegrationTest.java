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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ParcelamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void deveGerarParcelasComSomaExata() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Obra\",\"tipo\":\"DESPESA\"}");

        MvcResult r = mockMvc.perform(post("/api/v1/despesas/parceladas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Material\",\"valorTotal\":100.00,\"parcelas\":3,"
                            + "\"primeiroVencimento\":\"2026-08-10\",\"carteiraId\":" + carteira
                            + ",\"categoriaId\":" + categoria + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode parcelas = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(parcelas).hasSize(3);
        assertThat(parcelas.get(0).get("numeroParcela").asInt()).isEqualTo(1);
        assertThat(parcelas.get(2).get("numeroParcela").asInt()).isEqualTo(3);
        assertThat(parcelas.get(2).get("totalParcelas").asInt()).isEqualTo(3);

        BigDecimal soma = BigDecimal.ZERO;
        for (JsonNode p : parcelas) {
            soma = soma.add(p.get("valor").decimalValue());
        }
        assertThat(soma).isEqualByComparingTo("100.00");
    }

    @Test
    void deveRejeitarMenosDeDuasParcelas() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Obra\",\"tipo\":\"DESPESA\"}");

        mockMvc.perform(post("/api/v1/despesas/parceladas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Material\",\"valorTotal\":100.00,\"parcelas\":1,"
                            + "\"primeiroVencimento\":\"2026-08-10\",\"carteiraId\":" + carteira
                            + ",\"categoriaId\":" + categoria + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
