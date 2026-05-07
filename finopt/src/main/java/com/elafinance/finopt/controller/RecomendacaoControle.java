package com.elafinance.finopt.controller;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import com.elafinance.finopt.dto.RequisicaoOtimizacao;
import com.elafinance.finopt.usecase.EstrategiaOtimizacao;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
