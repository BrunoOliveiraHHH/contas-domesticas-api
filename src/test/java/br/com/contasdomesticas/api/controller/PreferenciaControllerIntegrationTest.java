package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class PreferenciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private long adminId() {
        return usuarioRepository.findByLogin("admin").orElseThrow().getId();
    }

    @Test
    void deveGravarEResolverGlobal() throws Exception {
        mockMvc.perform(put("/api/v1/preferencias/{chave}", "MOEDA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"BRL\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/preferencias/{chave}", "MOEDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("BRL"));
    }

    @Test
    void deveFazerFallbackParaGlobalQuandoUsuarioNaoTemValor() throws Exception {
        mockMvc.perform(put("/api/v1/preferencias/{chave}", "INICIO_MES")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"1\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/preferencias/{chave}", "INICIO_MES").param("usuarioId", String.valueOf(adminId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("1"))
                .andExpect(jsonPath("$.usuarioId").doesNotExist());
    }

    @Test
    void devePreferirValorDoUsuarioSobreOGlobal() throws Exception {
        long id = adminId();
        mockMvc.perform(put("/api/v1/preferencias/{chave}", "TEMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"claro\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/preferencias/{chave}", "TEMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"escuro\",\"usuarioId\":" + id + "}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/preferencias/{chave}", "TEMA").param("usuarioId", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("escuro"));
    }

    @Test
    void deveRetornar404ParaChaveInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/preferencias/{chave}", "NAO_EXISTE"))
                .andExpect(status().isNotFound());
    }
}
