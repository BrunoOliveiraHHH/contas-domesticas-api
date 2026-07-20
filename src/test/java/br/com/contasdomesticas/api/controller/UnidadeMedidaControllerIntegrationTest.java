package br.com.contasdomesticas.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class UnidadeMedidaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarUnidade() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Quilograma\",\"sigla\":\"kg\",\"tipo\":\"PESO\",\"fatorParaBase\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sigla").value("kg"));
    }

    @Test
    void deveRejeitarSiglaDuplicadaComConflito() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Litro\",\"sigla\":\"L\",\"tipo\":\"VOLUME\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Litro 2\",\"sigla\":\"L\",\"tipo\":\"VOLUME\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404ParaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/unidades-medida/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"sigla\":\"x\",\"tipo\":\"UNIDADE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
