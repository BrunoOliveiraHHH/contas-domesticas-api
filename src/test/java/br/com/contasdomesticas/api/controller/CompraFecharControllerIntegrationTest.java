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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class CompraFecharControllerIntegrationTest {

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
    void deveFecharGerandoUmaDespesaPorEstabelecimento() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Mercado\",\"tipo\":\"DESPESA\"}");
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Cesta\"}");
        long mercado = criar("/api/v1/mercados", "{\"nome\":\"Super X\",\"tipo\":\"SUPERMERCADO\"}");
        criar("/api/v1/produtos/" + produto + "/cotacoes", "{\"mercadoId\":" + mercado + ",\"precoUnitario\":10.00}");
        long lista = criar("/api/v1/listas-compra",
            "{\"nome\":\"Do mes\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + carteira + "}");
        long item = criar("/api/v1/listas-compra/" + lista + "/itens", "{\"produtoId\":" + produto + ",\"quantidade\":3}");
        mockMvc.perform(put("/api/v1/itens/{id}/escolha", item)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mercadoId\":" + mercado + "}"))
                .andExpect(status().isOk());

        MvcResult r = mockMvc.perform(post("/api/v1/listas-compra/{id}/fechar", lista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoriaId\":" + categoria + "}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode despesas = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(despesas).hasSize(1);
        assertThat(despesas.get(0).get("valor").decimalValue()).isEqualByComparingTo("30.00");

        mockMvc.perform(get("/api/v1/listas-compra/{id}", lista))
                .andExpect(jsonPath("$.status").value("FECHADA"));
    }

    @Test
    void deveRejeitarFecharComItemSemEstabelecimento() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Mercado\",\"tipo\":\"DESPESA\"}");
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Item\"}");
        long lista = criar("/api/v1/listas-compra",
            "{\"nome\":\"Sem escolha\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + carteira + "}");
        criar("/api/v1/listas-compra/" + lista + "/itens", "{\"produtoId\":" + produto + ",\"quantidade\":1}");

        mockMvc.perform(post("/api/v1/listas-compra/{id}/fechar", lista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoriaId\":" + categoria + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDuplicarListaCopiandoItens() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Reuso\"}");
        long lista = criar("/api/v1/listas-compra",
            "{\"nome\":\"Base\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + carteira + "}");
        criar("/api/v1/listas-compra/" + lista + "/itens", "{\"produtoId\":" + produto + ",\"quantidade\":2}");

        long nova = criar("/api/v1/listas-compra/" + lista + "/duplicar", "");

        mockMvc.perform(get("/api/v1/listas-compra/{id}/itens", nova))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
