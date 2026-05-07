package com.elafinance.finopt.domain;

public record Investimento (
        String nome,
        double custo,
        double retornoEsperado,
        String categoria,
        String corretora
) implements OpcaoFinanceira {}
