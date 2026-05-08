package com.elafinance.finopt.controller;

import com.elafinance.finopt.usecase.ServicoGemini;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ia")
public class ControleIA {

    private static final String SISTEMA_PROMPT =
            "Você é uma assistente financeira chamada Ela, especializada em autonomia financeira feminina. " +
            "Responda sempre em português brasileiro de forma clara, acolhedora e motivadora. " +
            "Foque em educação financeira, empoderamento e independência financeira. " +
            "Seja objetiva e prática nas respostas. " +
            "Nunca compartilhe dados de outras usuárias. " +
            "Se a pergunta não for sobre finanças ou bem-estar financeiro, diga que não pode responder sobre esse tema.";

    private final ServicoGemini servicoGemini;

    public ControleIA(ServicoGemini servicoGemini) {
        this.servicoGemini = servicoGemini;
    }

    @PostMapping("/explicar")
    public Map<String, String> explicar(@RequestBody Map<String, Object> corpo) {
        String mensagem = (String) corpo.getOrDefault("mensagem", "Como posso melhorar minha vida financeira?");
        String resposta = servicoGemini.consultar(SISTEMA_PROMPT, mensagem);
        return Map.of("status", "sucesso", "analise", resposta);
    }

    @PostMapping("/explicar-otimizacao")
    public Map<String, String> explicarOtimizacao(@RequestBody Map<String, Object> corpo) {
        String recomendacoes = corpo.getOrDefault("recomendacoes", "").toString();
        double pontuacao = Double.parseDouble(corpo.getOrDefault("pontuacao", "0").toString());
        String nivelRisco = (String) corpo.getOrDefault("nivelRisco", "INDEFINIDO");

        String mensagem =
                "Uma usuária recebeu as seguintes recomendações financeiras do sistema ElaFinance:\n\n" +
                "Recomendações: " + recomendacoes + "\n" +
                "Pontuação financeira: " + pontuacao + "\n" +
                "Nível de risco: " + nivelRisco + "\n\n" +
                "Explique em linguagem simples e motivadora por que essas escolhas foram recomendadas, " +
                "quais os benefícios para a autonomia financeira dela e o que ela pode esperar a longo prazo.";

        String resposta = servicoGemini.consultar(SISTEMA_PROMPT, mensagem);
        return Map.of("status", "sucesso", "analise", resposta);
    }
}
