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
class FormaPagamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarFormaSimples() throws Exception {
        mockMvc.perform(post("/api/v1/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Pix\",\"tipo\":\"PIX\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("PIX"));
    }

    @Test
    void deveExigirDiasNoCredito() throws Exception {
        mockMvc.perform(post("/api/v1/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Cartao\",\"tipo\":\"CREDITO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarCreditoComDias() throws Exception {
        mockMvc.perform(post("/api/v1/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Cartao\",\"tipo\":\"CREDITO\",\"diaFechamento\":10,\"diaVencimento\":17}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diaFechamento").value(10))
                .andExpect(jsonPath("$.diaVencimento").value(17));
    }

    @Test
    void deveIgnorarDiasForaDoCredito() throws Exception {
        mockMvc.perform(post("/api/v1/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Debito\",\"tipo\":\"DEBITO\",\"diaFechamento\":5,\"diaVencimento\":8}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diaFechamento").doesNotExist());
    }

    @Test
    void deveRetornar404ParaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/formas-pagamento/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":\"PIX\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }
}
