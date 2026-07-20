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
class ParametroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private void criar(String chave, String valor, String vigencia) throws Exception {
        mockMvc.perform(post("/api/v1/parametros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chave\":\"" + chave + "\",\"valor\":" + valor
                            + ",\"vigenciaInicio\":\"" + vigencia + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void deveCriarParametro() throws Exception {
        mockMvc.perform(post("/api/v1/parametros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chave\":\"CDI\",\"valor\":14.9,\"vigenciaInicio\":\"2026-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chave").value("CDI"));
    }

    @Test
    void deveResolverValorVigentePorData() throws Exception {
        criar("SELIC", "15", "2026-01-01");
        criar("SELIC", "12", "2026-06-01");

        mockMvc.perform(get("/api/v1/parametros/vigente/{chave}", "SELIC").param("data", "2026-03-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chave").value("SELIC"))
                .andExpect(jsonPath("$.vigenciaInicio").value("2026-01-01"));
    }

    @Test
    void deveResolverAliquotaIrPorPrazoEmDias() throws Exception {
        criar("IR_181_360", "20", "2026-01-01");

        mockMvc.perform(get("/api/v1/parametros/imposto-ir").param("dias", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chave").value("IR_181_360"))
                .andExpect(jsonPath("$.dias").value(200));
    }

    @Test
    void deveRetornar404SemVigencia() throws Exception {
        mockMvc.perform(get("/api/v1/parametros/vigente/{chave}", "INEXISTENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/parametros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chave\":\"\",\"valor\":1,\"vigenciaInicio\":\"2026-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
