package com.elafinance.finopt.domain;

public record AnaliseFinanceira(
        String recomendacao,
        String nivelRisco,
        double pontuacao
) {}
