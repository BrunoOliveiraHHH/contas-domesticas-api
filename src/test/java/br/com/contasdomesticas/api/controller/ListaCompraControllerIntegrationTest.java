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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ListaCompraControllerIntegrationTest {

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

    @Test
    void deveCriarListaAberta() throws Exception {
        long c = carteira();
        mockMvc.perform(post("/api/v1/listas-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Feira do mes\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + c + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @Test
    void deveFiltrarPorStatus() throws Exception {
        long c = carteira();
        criar("/api/v1/listas-compra",
            "{\"nome\":\"Obra\",\"tipo\":\"CONSTRUCAO\",\"carteiraId\":" + c + "}");

        mockMvc.perform(get("/api/v1/listas-compra").param("status", "ABERTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveBuscarAtualizarERemover() throws Exception {
        long c = carteira();
        long id = criar("/api/v1/listas-compra",
            "{\"nome\":\"Semana\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + c + "}");

        mockMvc.perform(get("/api/v1/listas-compra/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Semana"));

        mockMvc.perform(put("/api/v1/listas-compra/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Semana 2\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + c + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Semana 2"));

        mockMvc.perform(delete("/api/v1/listas-compra/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/listas-compra/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        long c = carteira();
        mockMvc.perform(post("/api/v1/listas-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":\"MANTIMENTOS\",\"carteiraId\":" + c + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
