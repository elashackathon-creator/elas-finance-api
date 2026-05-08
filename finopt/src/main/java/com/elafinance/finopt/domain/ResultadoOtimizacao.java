package com.elafinance.finopt.domain;

import java.util.List;

public sealed interface ResultadoOtimizacao permits ResultadoOtimizacao.ComRecomendacoes, ResultadoOtimizacao.SemOpcoes {

    record ComRecomendacoes(
            List<OpcaoFinanceira> opcoes,
            double custoTotal,
            double retornoTotal
    ) implements ResultadoOtimizacao {}

    record SemOpcoes(String motivo) implements ResultadoOtimizacao {}
}
