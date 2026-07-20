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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class DespesaControllerIntegrationTest {

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

    private String despesaBody(long c, long cat, String vencimento) {
        String venc = vencimento != null ? ",\"dataVencimento\":\"" + vencimento + "\"" : "";
        return "{\"descricao\":\"Conta\",\"valor\":200.00,\"dataCompetencia\":\"2026-07-05\","
            + "\"carteiraId\":" + c + ",\"categoriaId\":" + cat + venc + "}";
    }

    @Test
    void deveCriarDespesaPendente() throws Exception {
        long c = carteira();
        long cat = categoria("DESPESA");

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(despesaBody(c, cat, "2026-12-31")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveMarcarComoAtrasadaQuandoVencidaSemPagamento() throws Exception {
        long c = carteira();
        long cat = categoria("DESPESA");

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(despesaBody(c, cat, "2026-01-01")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATRASADO"));
    }

    @Test
    void deveMarcarComoPaga() throws Exception {
        long c = carteira();
        long cat = categoria("DESPESA");
        long id = criar("/api/v1/despesas", despesaBody(c, cat, "2026-12-31"));

        mockMvc.perform(post("/api/v1/despesas/{id}/pagar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"))
                .andExpect(jsonPath("$.dataPagamento").exists());
    }

    @Test
    void deveRejeitarCategoriaDeTipoIncompativel() throws Exception {
        long c = carteira();
        long catReceita = categoria("RECEITA");

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(despesaBody(c, catReceita, "2026-12-31")))
                .andExpect(status().isBadRequest());
    }
}
