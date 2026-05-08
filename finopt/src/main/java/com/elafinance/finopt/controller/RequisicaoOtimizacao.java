package com.elafinance.finopt.controller;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record RequisicaoOtimizacao(
        @NotNull(message = "A lista de opções não pode ser nula")
        @NotEmpty(message = "Informe pelo menos uma opção financeira")
        List<OpcaoFinanceira> opcoes,

        @Positive(message = "O orçamento precisa ser maior que zero")
        double orcamento
) {}
