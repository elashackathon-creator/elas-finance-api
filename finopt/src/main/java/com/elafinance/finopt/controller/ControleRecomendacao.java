package com.elafinance.finopt.controller;

import com.elafinance.finopt.domain.AnaliseFinanceira;
import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.domain.ResultadoOtimizacao;
import com.elafinance.finopt.infraestrutura.HistoricoOtimizacao;
import com.elafinance.finopt.infraestrutura.HistoricoOtimizacaoRepositorio;
import com.elafinance.finopt.usecase.EstrategiaOtimizacao;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/otimizar")
public class ControleRecomendacao {

    private final EstrategiaOtimizacao estrategia;
    private final HistoricoOtimizacaoRepositorio repositorio;

    public ControleRecomendacao(EstrategiaOtimizacao estrategia, HistoricoOtimizacaoRepositorio repositorio) {
        this.estrategia = estrategia;
        this.repositorio = repositorio;
    }

    @PostMapping
    public RespostaOtimizacao otimizar(@RequestBody @Valid RequisicaoOtimizacao requisicao) {
        ResultadoOtimizacao resultado = estrategia.otimizar(requisicao.opcoes(), requisicao.orcamento());

        return switch (resultado) {
            case ResultadoOtimizacao.ComRecomendacoes r -> {
                double pontuacao = (r.retornoTotal() / r.custoTotal()) * 100;
                String risco = calcularRisco(pontuacao);

                repositorio.save(new HistoricoOtimizacao(
                        LocalDateTime.now(),
                        requisicao.orcamento(),
                        r.custoTotal(),
                        r.retornoTotal(),
                        pontuacao,
                        risco,
                        r.opcoes().size()
                ));

                yield new RespostaOtimizacao(
                        "Plano financeiro otimizado com sucesso.",
                        r.custoTotal(),
                        r.retornoTotal(),
                        new AnaliseFinanceira(gerarRecomendacao(risco, r.opcoes()), risco, pontuacao),
                        r.opcoes(),
                        gerarCriterios(requisicao.orcamento(), r.opcoes())
                );
            }
            case ResultadoOtimizacao.SemOpcoes s -> new RespostaOtimizacao(
                    s.motivo(), 0, 0,
                    new AnaliseFinanceira("Sem recomendações possíveis para o orçamento informado.", "INDEFINIDO", 0),
                    List.of(),
                    List.of("Nenhuma das opções informadas cabe no orçamento de R$" +
                            String.format("%.2f", requisicao.orcamento()) + " ou possui retorno esperado positivo.")
            );
        };
    }

    @GetMapping("/historico")
    public List<HistoricoOtimizacao> historico() {
        return repositorio.findAll();
    }

    private String calcularRisco(double pontuacao) {
        if (pontuacao >= 80) return "BAIXO";
        if (pontuacao >= 50) return "MÉDIO";
        return "ALTO";
    }

    private String gerarRecomendacao(String risco, List<OpcaoFinanceira> opcoes) {
        long investimentos = opcoes.stream().filter(o -> o.categoria().equals("INVESTIMENTO") || o.categoria().equals("EDUCACAO")).count();
        if (investimentos > 0) return "Plano focado em crescimento: prioriza educação e investimentos.";
        if (risco.equals("BAIXO")) return "Plano equilibrado com bom retorno esperado.";
        if (risco.equals("MÉDIO")) return "Plano com retorno moderado. Considere revisar as opções.";
        return "Atenção: plano com retorno abaixo do custo. Revise suas prioridades.";
    }

    private List<String> gerarCriterios(double orcamento, List<OpcaoFinanceira> opcoesSelecionadas) {
        return List.of(
                "Critério 1: eliminadas opções com custo superior ao orçamento de R$" + String.format("%.2f", orcamento),
                "Critério 2: eliminadas opções com retorno esperado zero ou negativo",
                "Critério 3: opções ordenadas por maior retorno esperado (algoritmo da mochila — maximização de valor)",
                "Critério 4: selecionadas as " + opcoesSelecionadas.size() + " opção(ões) de maior retorno que cabem no orçamento"
        );
    }
}
