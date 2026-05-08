package com.elafinance.finopt.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ServicoGemini {

    private static final Logger log = LoggerFactory.getLogger(ServicoGemini.class);
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final List<String> MODELOS = List.of(
            "llama-3.3-70b-versatile",
            "mixtral-8x7b-32768",
            "gemma2-9b-it"
    );

    private final RestClient restClient;
    private final String apiKey;

    public ServicoGemini(@Value("${groq.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    public String consultar(String sistemaPrompt, String mensagem) {
        for (String modelo : MODELOS) {
            try {
                String resposta = chamarApi(modelo, sistemaPrompt, mensagem);
                log.info("IA respondeu via modelo: {}", modelo);
                return resposta;
            } catch (Exception e) {
                log.warn("Modelo {} falhou ({}), tentando próximo...", modelo, e.getMessage().split(":")[0]);
            }
        }
        log.error("Todos os modelos falharam.");
        return "Não foi possível obter uma resposta da IA no momento. Tente novamente em breve.";
    }

    @SuppressWarnings("unchecked")
    private String chamarApi(String modelo, String sistemaPrompt, String mensagem) {
        Map<String, Object> requisicao = Map.of(
                "model", modelo,
                "messages", List.of(
                        Map.of("role", "system", "content", sistemaPrompt),
                        Map.of("role", "user", "content", mensagem)
                )
        );

        Map<String, Object> resposta = restClient.post()
                .uri(GROQ_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requisicao)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) resposta.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");
        return content == null ? "" : content.replace("\n", " ").replaceAll(" {2,}", " ").trim();
    }
}
