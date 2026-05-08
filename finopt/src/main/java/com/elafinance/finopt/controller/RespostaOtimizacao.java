package com.elafinance.finopt.controller;

import com.elafinance.finopt.domain.AnaliseFinanceira;
import com.elafinance.finopt.domain.OpcaoFinanceira;

import java.util.List;

public record RespostaOtimizacao(
        String mensagem,
        double custoTotal,
        double retornoTotal,
        AnaliseFinanceira analise,
        List<OpcaoFinanceira> recomendacoes,
        List<String> criterios
) {}
