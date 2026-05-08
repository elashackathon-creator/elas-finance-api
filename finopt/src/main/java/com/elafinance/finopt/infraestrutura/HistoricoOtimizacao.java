package com.elafinance.finopt.infraestrutura;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_otimizacao")
@Getter
@NoArgsConstructor
public class HistoricoOtimizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;
    private double orcamento;
    private double custoTotal;
    private double retornoTotal;
    private double pontuacao;
    private String nivelRisco;
    private int quantidadeRecomendacoes;

    public HistoricoOtimizacao(LocalDateTime dataHora, double orcamento, double custoTotal,
                                double retornoTotal, double pontuacao, String nivelRisco,
                                int quantidadeRecomendacoes) {
        this.dataHora = dataHora;
        this.orcamento = orcamento;
        this.custoTotal = custoTotal;
        this.retornoTotal = retornoTotal;
        this.pontuacao = pontuacao;
        this.nivelRisco = nivelRisco;
        this.quantidadeRecomendacoes = quantidadeRecomendacoes;
    }
}
