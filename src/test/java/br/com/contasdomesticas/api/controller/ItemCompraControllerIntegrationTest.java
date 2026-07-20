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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ItemCompraControllerIntegrationTest {

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
        return criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
    }

    private long lista(long carteira) throws Exception {
        return criar("/api/v1/listas-compra",
            "{\"nome\":\"Feira\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + carteira + "}");
    }

    @Test
    void deveAdicionarItem() throws Exception {
        long lista = lista(carteira());
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Arroz\"}");

        mockMvc.perform(post("/api/v1/listas-compra/{listaId}/itens", lista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtoId\":" + produto + ",\"quantidade\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.produtoId").value(produto))
                .andExpect(jsonPath("$.comprado").value(false));
    }

    @Test
    void deveEscolherEstabelecimentoComCotacao() throws Exception {
        long lista = lista(carteira());
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Feijao\"}");
        long mercado = criar("/api/v1/mercados", "{\"nome\":\"Super\",\"tipo\":\"SUPERMERCADO\"}");
        criar("/api/v1/produtos/" + produto + "/cotacoes", "{\"mercadoId\":" + mercado + ",\"precoUnitario\":7.50}");
        long item = criar("/api/v1/listas-compra/" + lista + "/itens", "{\"produtoId\":" + produto + ",\"quantidade\":1}");

        mockMvc.perform(put("/api/v1/itens/{id}/escolha", item)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mercadoId\":" + mercado + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mercadoEscolhidoId").value(mercado))
                .andExpect(jsonPath("$.precoUnitario").exists());
    }

    @Test
    void deveRejeitarEscolhaSemCotacao() throws Exception {
        long lista = lista(carteira());
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Sal\"}");
        long mercado = criar("/api/v1/mercados", "{\"nome\":\"Mercadinho\",\"tipo\":\"MERCEARIA\"}");
        long item = criar("/api/v1/listas-compra/" + lista + "/itens", "{\"produtoId\":" + produto + ",\"quantidade\":1}");

        mockMvc.perform(put("/api/v1/itens/{id}/escolha", item)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mercadoId\":" + mercado + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404AoEscolherItemInexistente() throws Exception {
        mockMvc.perform(put("/api/v1/itens/{id}/escolha", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mercadoId\":1}"))
                .andExpect(status().isNotFound());
    }
}
