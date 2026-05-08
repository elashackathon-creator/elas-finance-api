package com.elafinance.finopt;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.domain.ResultadoOtimizacao;
import com.elafinance.finopt.usecase.OtimizacaoSimples;
import com.elafinance.finopt.usecase.ServicoGemini;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class FinoptApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OtimizacaoSimples otimizacao;

    @MockitoBean
    private ServicoGemini servicoGemini;

    @Test
    void contextLoads() {
    }

    @Test
    void deveRetornarComRecomendacoesQuandoHaOpcoesDentroDoOrcamento() {
        List<OpcaoFinanceira> opcoes = List.of(
                new OpcaoFinanceira("Curso", 400.0, 350.0, "EDUCACAO"),
                new OpcaoFinanceira("Streaming", 60.0, 40.0, "LAZER"),
                new OpcaoFinanceira("Viagem", 3000.0, 500.0, "LAZER")
        );

        ResultadoOtimizacao resultado = otimizacao.otimizar(opcoes, 500.0);

        assertThat(resultado).isInstanceOf(ResultadoOtimizacao.ComRecomendacoes.class);
        ResultadoOtimizacao.ComRecomendacoes com = (ResultadoOtimizacao.ComRecomendacoes) resultado;
        assertThat(com.opcoes()).hasSize(2);
        assertThat(com.opcoes()).noneMatch(op -> op.custo() > 500.0);
    }

    @Test
    void deveRetornarSemOpcoesQuandoNadaCabeNoOrcamento() {
        List<OpcaoFinanceira> opcoes = List.of(
                new OpcaoFinanceira("Curso caro", 1000.0, 800.0, "EDUCACAO")
        );

        ResultadoOtimizacao resultado = otimizacao.otimizar(opcoes, 100.0);

        assertThat(resultado).isInstanceOf(ResultadoOtimizacao.SemOpcoes.class);
    }

    @Test
    void deveOrdenarPorMaiorRetorno() {
        List<OpcaoFinanceira> opcoes = List.of(
                new OpcaoFinanceira("Streaming", 60.0, 40.0, "LAZER"),
                new OpcaoFinanceira("Curso", 400.0, 350.0, "EDUCACAO")
        );

        ResultadoOtimizacao resultado = otimizacao.otimizar(opcoes, 500.0);
        ResultadoOtimizacao.ComRecomendacoes com = (ResultadoOtimizacao.ComRecomendacoes) resultado;

        assertThat(com.opcoes().get(0).nome()).isEqualTo("Curso");
    }

    @Test
    void deveClassificarCategoriaCorretamente() {
        assertThat(otimizacao.classificarCategoria("EDUCACAO")).isEqualTo("investimento em capacitação");
        assertThat(otimizacao.classificarCategoria("LAZER")).isEqualTo("gasto de conforto");
        assertThat(otimizacao.classificarCategoria("SAUDE")).isEqualTo("gasto com saúde");
        assertThat(otimizacao.classificarCategoria("OUTRO")).isEqualTo("gasto geral");
    }

    @Test
    void postOtimizarDeveRetornar200ComRecomendacoes() throws Exception {
        String corpo = """
                {
                  "orcamento": 500,
                  "opcoes": [
                    {"nome": "Curso", "custo": 400, "retornoEsperado": 350, "categoria": "EDUCACAO"},
                    {"nome": "Streaming", "custo": 60, "retornoEsperado": 40, "categoria": "LAZER"}
                  ]
                }
                """;

        mockMvc.perform(post("/otimizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Plano financeiro otimizado com sucesso."))
                .andExpect(jsonPath("$.recomendacoes").isArray())
                .andExpect(jsonPath("$.analise.nivelRisco").exists())
                .andExpect(jsonPath("$.custoTotal").isNumber());
    }

    @Test
    void postOtimizarDeveRetornar400QuandoOrcamentoInvalido() throws Exception {
        String corpo = """
                {
                  "orcamento": -100,
                  "opcoes": [
                    {"nome": "Curso", "custo": 400, "retornoEsperado": 350, "categoria": "EDUCACAO"}
                  ]
                }
                """;

        mockMvc.perform(post("/otimizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.orcamento").exists());
    }

    @Test
    void getHistoricoDeveRetornar200() throws Exception {
        mockMvc.perform(get("/otimizar/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void postExplicarDeveRetornar200() throws Exception {
        when(servicoGemini.consultar(anyString(), anyString()))
                .thenReturn("Para melhorar sua organização financeira, comece registrando todos os seus gastos diários.");

        mockMvc.perform(post("/ia/explicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\": \"Quero melhorar minha organização financeira\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sucesso"))
                .andExpect(jsonPath("$.analise").isString());
    }
}
