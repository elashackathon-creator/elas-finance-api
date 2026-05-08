package com.elafinance.finopt.domain;

public record OpcaoFinanceira(
        String nome,
        double custo,
        double retornoEsperado,
        String categoria
) {}
