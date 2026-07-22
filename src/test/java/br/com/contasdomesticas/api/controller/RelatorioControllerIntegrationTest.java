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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class RelatorioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long carteira() throws Exception {
        return criar("/api/v1/carteiras", "{\"nome\":\"Rel\",\"tipo\":\"FAMILIAR\"}");
    }

    private long categoria(String tipo) throws Exception {
        return criar("/api/v1/categorias", "{\"nome\":\"Cat " + tipo + "\",\"tipo\":\"" + tipo + "\"}");
    }

    @Test
    void deveCalcularSaldoDoMes() throws Exception {
        long c = carteira();
        long catReceita = categoria("RECEITA");
        long catDespesa = categoria("DESPESA");
        criar("/api/v1/receitas", "{\"descricao\":\"Salario\",\"valor\":5000.00,\"dataCompetencia\":\"2027-05-05\","
            + "\"dataInicio\":\"2027-05-01\",\"carteiraId\":" + c + ",\"categoriaId\":" + catReceita + "}");
        criar("/api/v1/despesas", "{\"descricao\":\"Aluguel\",\"valor\":2000.00,\"dataCompetencia\":\"2027-05-10\","
            + "\"carteiraId\":" + c + ",\"categoriaId\":" + catDespesa + "}");

        MvcResult r = mockMvc.perform(get("/api/v1/relatorios/saldo")
                        .param("periodo", "2027-05")
                        .param("carteira", String.valueOf(c)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode saldo = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(saldo.get("receitas").decimalValue()).isEqualByComparingTo("5000.00");
        assertThat(saldo.get("despesas").decimalValue()).isEqualByComparingTo("2000.00");
        assertThat(saldo.get("saldo").decimalValue()).isEqualByComparingTo("3000.00");
    }

    @Test
    void deveAgruparGastosPorCategoria() throws Exception {
        long c = carteira();
        long catDespesa = categoria("DESPESA");
        criar("/api/v1/despesas", "{\"descricao\":\"Mercado\",\"valor\":800.00,\"dataCompetencia\":\"2027-06-08\","
            + "\"carteiraId\":" + c + ",\"categoriaId\":" + catDespesa + "}");

        MvcResult r = mockMvc.perform(get("/api/v1/relatorios/por-categoria")
                        .param("periodo", "2027-06").param("tipo", "DESPESA"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode itens = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).get("total").decimalValue()).isEqualByComparingTo("800.00");
        assertThat(itens.get(0).get("percentual").decimalValue()).isEqualByComparingTo("100.00");
    }
}
