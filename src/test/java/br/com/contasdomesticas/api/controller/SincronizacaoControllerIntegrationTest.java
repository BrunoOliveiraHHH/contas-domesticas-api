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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class SincronizacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private JsonNode merge(String body) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/sync/mercados")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString());
    }

    private String mercado(String uuid, String nome, int versao, boolean deletado) {
        return "[{\"uuid\":\"" + uuid + "\",\"nome\":\"" + nome + "\",\"tipo\":\"SUPERMERCADO\","
            + "\"ativo\":true,\"versao\":" + versao + ",\"deletado\":" + deletado + "}]";
    }

    @Test
    void mergeDeveAplicarLastWriteWinsPelaVersao() throws Exception {
        String uuid = "11111111-1111-1111-1111-111111111111";
        merge(mercado(uuid, "Merc A", 1, false));
        merge(mercado(uuid, "Merc B", 2, false));
        // versao menor (mais antiga) deve ser ignorada
        JsonNode resposta = merge(mercado(uuid, "Merc C", 1, false));

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).get("nome").asText()).isEqualTo("Merc B");
        assertThat(resposta.get(0).get("versao").asLong()).isEqualTo(2);
    }

    @Test
    void mergeDevePropagarTombstone() throws Exception {
        String uuid = "22222222-2222-2222-2222-222222222222";
        merge(mercado(uuid, "Para deletar", 1, false));
        JsonNode resposta = merge(mercado(uuid, "Para deletar", 2, true));

        assertThat(resposta.get(0).get("deletado").asBoolean()).isTrue();
    }

    @Test
    void deltaDeveRetornarRegistrosAlteradosDesde() throws Exception {
        String uuid = "33333333-3333-3333-3333-333333333333";
        merge(mercado(uuid, "Novo via sync", 1, false));

        MvcResult r = mockMvc.perform(get("/api/v1/sync/mercados").param("desde", "2000-01-01T00:00:00Z"))
                .andExpect(status().isOk()).andReturn();
        JsonNode delta = objectMapper.readTree(r.getResponse().getContentAsString());

        boolean encontrou = false;
        for (JsonNode m : delta) {
            if (uuid.equals(m.get("uuid").asText())) {
                encontrou = true;
                break;
            }
        }
        assertThat(encontrou).isTrue();
    }
}
