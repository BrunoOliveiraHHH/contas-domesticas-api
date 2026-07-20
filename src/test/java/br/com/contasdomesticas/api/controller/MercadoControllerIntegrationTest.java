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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class MercadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarMercadoArmazem() throws Exception {
        mockMvc.perform(post("/api/v1/mercados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Armazem do Ze\",\"tipo\":\"ARMAZEM\",\"bairro\":\"Centro\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ARMAZEM"))
                .andExpect(jsonPath("$.bairro").value("Centro"));
    }

    @Test
    void deveBuscarAtualizarERemover() throws Exception {
        MvcResult criado = mockMvc.perform(post("/api/v1/mercados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Extra\",\"tipo\":\"SUPERMERCADO\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        long id = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/mercados/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Extra"));

        mockMvc.perform(put("/api/v1/mercados/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Extra Hiper\",\"tipo\":\"SUPERMERCADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Extra Hiper"));

        mockMvc.perform(delete("/api/v1/mercados/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/mercados/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/mercados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":\"OUTRO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
