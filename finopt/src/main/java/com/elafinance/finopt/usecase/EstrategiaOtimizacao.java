package com.elafinance.finopt.usecase;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.domain.ResultadoOtimizacao;
import java.util.List;

public interface EstrategiaOtimizacao {
    ResultadoOtimizacao otimizar(List<OpcaoFinanceira> opcoes, double orcamento);
}
