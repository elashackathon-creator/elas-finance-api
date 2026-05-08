package com.elafinance.finopt.usecase;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.domain.ResultadoOtimizacao;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OtimizacaoSimples implements EstrategiaOtimizacao {

    @Override
    public ResultadoOtimizacao otimizar(List<OpcaoFinanceira> opcoes, double orcamento) {
        List<OpcaoFinanceira> filtradas = opcoes.stream()
                .filter(op -> op.custo() <= orcamento && op.retornoEsperado() > 0)
                .sorted(Comparator.comparingDouble(OpcaoFinanceira::retornoEsperado).reversed())
                .toList();

        if (filtradas.isEmpty()) {
            return new ResultadoOtimizacao.SemOpcoes("Nenhuma opção se encaixa no orçamento informado.");
        }

        double custoTotal = filtradas.stream().mapToDouble(OpcaoFinanceira::custo).sum();
        double retornoTotal = filtradas.stream().mapToDouble(OpcaoFinanceira::retornoEsperado).sum();

        return new ResultadoOtimizacao.ComRecomendacoes(filtradas, custoTotal, retornoTotal);
    }

    public String classificarCategoria(String categoria) {
        return switch (categoria) {
            case "EDUCACAO" -> "investimento em capacitação";
            case "LAZER" -> "gasto de conforto";
            case "ALIMENTACAO" -> "gasto essencial";
            case "SAUDE" -> "gasto com saúde";
            case "INVESTIMENTO" -> "aplicação financeira";
            default -> "gasto geral";
        };
    }
}
