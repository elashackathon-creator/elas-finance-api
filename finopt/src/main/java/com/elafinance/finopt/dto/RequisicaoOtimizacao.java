package com.elafinance.finopt.dto;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.usecase.EstrategiaOtimizacao;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public record RequisicaoOtimizacao(List<OpcaoFinanceira> opcoes, double orcamento) {

    @RestController
    @RequestMapping("/otimizar")
    public class RecomendacaoControle {

        private final EstrategiaOtimizacao servico;

        public RecomendacaoControle(EstrategiaOtimizacao servico) {
            this.servico = servico;
        }

        @PostMapping
        public List<OpcaoFinanceira> obterCarteira(
                @RequestBody RequisicaoOtimizacao req
        ) {

            return servico.otimizar(
                    req.opcoes(),
                    req.orcamento()
            );
        }

    }
}