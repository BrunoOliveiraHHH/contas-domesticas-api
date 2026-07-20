package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.repository.UsuarioRepository;
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class RateioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private long criar(String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long despesaDe100() throws Exception {
        long carteira = criar("/api/v1/carteiras", "{\"nome\":\"Comum\",\"tipo\":\"FAMILIAR\"}");
        long categoria = criar("/api/v1/categorias", "{\"nome\":\"Mercado\",\"tipo\":\"DESPESA\"}");
        return criar("/api/v1/despesas",
            "{\"descricao\":\"Feira\",\"valor\":100.00,\"dataCompetencia\":\"2026-07-05\",\"carteiraId\":"
                + carteira + ",\"categoriaId\":" + categoria + "}");
    }

    private long segundoUsuario(String login) throws Exception {
        return criar("/api/v1/usuarios",
            "{\"login\":\"" + login + "\",\"nomeExibicao\":\"Karla\",\"senha\":\"karla123\"}");
    }

    private static int seq = 0;

    private String nomeUnico() {
        return "karla" + (++seq);
    }

    @Test
    void deveRatearIgualmenteEValoresSomaremOTotal() throws Exception {
        long despesa = despesaDe100();
        long admin = usuarioRepository.findByLogin("admin").orElseThrow().getId();
        long karla = segundoUsuario(nomeUnico());

        MvcResult r = mockMvc.perform(post("/api/v1/despesas/{id}/rateio", despesa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"IGUAL\",\"participantes\":[{\"usuarioId\":" + admin
                            + "},{\"usuarioId\":" + karla + "}]}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode participantes = objectMapper.readTree(r.getResponse().getContentAsString()).get("participantes");
        assertThat(participantes).hasSize(2);
        BigDecimal soma = BigDecimal.ZERO;
        for (JsonNode p : participantes) {
            soma = soma.add(p.get("valor").decimalValue());
        }
        assertThat(soma).isEqualByComparingTo("100.00");
    }

    @Test
    void deveRejeitarSomaDePercentuaisDiferenteDe100() throws Exception {
        long despesa = despesaDe100();
        long admin = usuarioRepository.findByLogin("admin").orElseThrow().getId();
        long karla = segundoUsuario(nomeUnico());

        mockMvc.perform(post("/api/v1/despesas/{id}/rateio", despesa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"CUSTOM\",\"participantes\":[{\"usuarioId\":" + admin
                            + ",\"percentual\":30},{\"usuarioId\":" + karla + ",\"percentual\":30}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsolidarAcertoDoPeriodo() throws Exception {
        long despesa = despesaDe100();
        long admin = usuarioRepository.findByLogin("admin").orElseThrow().getId();
        long karla = segundoUsuario(nomeUnico());

        mockMvc.perform(post("/api/v1/despesas/{id}/rateio", despesa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"IGUAL\",\"participantes\":[{\"usuarioId\":" + admin
                            + "},{\"usuarioId\":" + karla + "}]}"))
                .andExpect(status().isCreated());

        MvcResult r = mockMvc.perform(get("/api/v1/rateios/acerto").param("periodo", "2026-07"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode acerto = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(acerto.isArray()).isTrue();
        assertThat(acerto.size()).isGreaterThanOrEqualTo(2);
    }
}
