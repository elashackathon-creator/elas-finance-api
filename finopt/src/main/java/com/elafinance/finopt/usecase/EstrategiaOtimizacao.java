package com.elafinance.finopt.usecase;

import com.elafinance.finopt.domain.OpcaoFinanceira;

import java.util.List;

public interface EstrategiaOtimizacao {
    List<OpcaoFinanceira> otimizar(List<OpcaoFinanceira> opcoes, double orcamento);
}
