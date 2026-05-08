package com.elafinance.finopt;

import com.elafinance.finopt.config.ConfiguracaoSeguranca;
import com.elafinance.finopt.controller.ControleRecomendacao;
import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.domain.ResultadoOtimizacao;
import com.elafinance.finopt.infraestrutura.HistoricoOtimizacaoRepositorio;
import com.elafinance.finopt.usecase.EstrategiaOtimizacao;
import com.elafinance.finopt.usecase.ServicoAutenticacao;
import com.elafinance.finopt.usecase.ServicoJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControleRecomendacao.class)
@Import(ConfiguracaoSeguranca.class)
@WithMockUser
class ControleRecomendacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstrategiaOtimizacao estrategia;

    @MockitoBean
    private HistoricoOtimizacaoRepositorio repositorio;

    @MockitoBean
    private ServicoJWT servicoJWT;

    @MockitoBean
    private ServicoAutenticacao servicoAutenticacao;

    @Test
    void deveRetornarRespostaOtimizadaComMock() throws Exception {
        List<OpcaoFinanceira> opcoesMock = List.of(
                new OpcaoFinanceira("Curso", 400.0, 350.0, "EDUCACAO")
        );
        when(estrategia.otimizar(any(), anyDouble()))
                .thenReturn(new ResultadoOtimizacao.ComRecomendacoes(opcoesMock, 400.0, 350.0));
        when(repositorio.save(any())).thenReturn(new com.elafinance.finopt.infraestrutura.HistoricoOtimizacao());

        String corpo = """
                {
                  "orcamento": 500,
                  "opcoes": [
                    {"nome": "Curso", "custo": 400, "retornoEsperado": 350, "categoria": "EDUCACAO"}
                  ]
                }
                """;

        mockMvc.perform(post("/otimizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Plano financeiro otimizado com sucesso."))
                .andExpect(jsonPath("$.recomendacoes[0].nome").value("Curso"))
                .andExpect(jsonPath("$.custoTotal").value(400.0));
    }

    @Test
    void deveRetornarMensagemQuandoSemOpcoes() throws Exception {
        when(estrategia.otimizar(any(), anyDouble()))
                .thenReturn(new ResultadoOtimizacao.SemOpcoes("Nenhuma opção se encaixa no orçamento informado."));

        String corpo = """
                {
                  "orcamento": 10,
                  "opcoes": [
                    {"nome": "Curso caro", "custo": 5000, "retornoEsperado": 800, "categoria": "EDUCACAO"}
                  ]
                }
                """;

        mockMvc.perform(post("/otimizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Nenhuma opção se encaixa no orçamento informado."))
                .andExpect(jsonPath("$.recomendacoes").isEmpty());
    }
}
