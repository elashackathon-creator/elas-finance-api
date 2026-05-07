package com.elafinance.finopt.domain;

public record CorteDeGasto (
        String nome,
        double custo,
        double retornoEsperado,
        String categoria,
        String prioridade
) implements OpcaoFinanceira {}