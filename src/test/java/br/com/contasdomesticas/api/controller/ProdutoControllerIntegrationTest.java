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
class ProdutoControllerIntegrationTest {

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
    void deveCriarProduto() throws Exception {
        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Arroz 5kg\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Arroz 5kg"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveCriarProdutoComCategoriaEUnidade() throws Exception {
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Mercado\",\"tipo\":\"DESPESA\"}");
        long unidade = criar("/api/v1/unidades-medida", "{\"nome\":\"Quilograma\",\"sigla\":\"kgp\",\"tipo\":\"PESO\"}");

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Feijao\",\"categoriaId\":" + categoria
                            + ",\"unidadeMedidaPadraoId\":" + unidade + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoriaId").value(categoria))
                .andExpect(jsonPath("$.unidadeMedidaPadraoId").value(unidade));
    }

    @Test
    void deveAdicionarECompararCotacoesPorPreco() throws Exception {
        long produto = criar("/api/v1/produtos", "{\"nome\":\"Detergente\"}");
        long mercadoA = criar("/api/v1/mercados", "{\"nome\":\"Super A\",\"tipo\":\"SUPERMERCADO\"}");
        long mercadoB = criar("/api/v1/mercados", "{\"nome\":\"Armazem B\",\"tipo\":\"ARMAZEM\"}");

        criar("/api/v1/produtos/" + produto + "/cotacoes",
            "{\"mercadoId\":" + mercadoA + ",\"precoUnitario\":10.00}");
        criar("/api/v1/produtos/" + produto + "/cotacoes",
            "{\"mercadoId\":" + mercadoB + ",\"precoUnitario\":8.00}");

        MvcResult r = mockMvc.perform(get("/api/v1/produtos/{id}/cotacoes", produto))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode cotacoes = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(cotacoes).hasSize(2);
        // ordenado por preco crescente: o mais barato primeiro
        assertThat(cotacoes.get(0).get("precoUnitario").decimalValue()).isEqualByComparingTo("8.00");
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/produtos/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
