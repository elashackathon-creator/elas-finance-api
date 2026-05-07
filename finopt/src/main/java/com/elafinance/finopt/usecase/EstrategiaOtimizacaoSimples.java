package com.elafinance.finopt.usecase;

import com.elafinance.finopt.domain.OpcaoFinanceira;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EstrategiaOtimizacaoSimples implements EstrategiaOtimizacao {

    @Override
    public List<OpcaoFinanceira> otimizar(List<OpcaoFinanceira> opcoes, double orcamento) {
        List<OpcaoFinanceira> validas = opcoes.stream()
                .filter(o -> o.custo() > 0)
                .toList();

        int n = validas.size();
        int capacidade = (int) orcamento;
        double[][] tabela = new double[n + 1][capacidade + 1];

        for (int i = 1; i <= n; i++) {
            OpcaoFinanceira item = validas.get(i - 1);

            String log = switch (item) {
                case com.elafinance.finopt.domain.Investimento inv -> "Analisando investimento: " + inv.nome();
                case com.elafinance.finopt.domain.CorteDeGasto corte -> "Analisando corte: " + corte.nome();
            };
            System.out.println(log);

            for (int j = 0; j <= capacidade; j++) {
                if (item.custo() <= j) {
                    tabela[i][j] = Math.max(tabela[i - 1][j],
                            tabela[i - 1][j - (int)item.custo()] + item.retornoEsperado());
                } else {
                    tabela[i][j] = tabela[i - 1][j];
                }
            }
        }

        List<OpcaoFinanceira> selecionadas = new ArrayList<>();
        int res = capacidade;
        for (int i = n; i > 0; i--) {
            if (tabela[i][res] != tabela[i - 1][res]) {
                selecionadas.add(validas.get(i - 1));
                res -= (int) validas.get(i - 1).custo();
            }
        }
        return selecionadas;
    }
}