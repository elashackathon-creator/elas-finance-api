package com.elafinance.finopt.controller;


import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ia")
public class IAControle {

    @PostMapping("/explicar")
    public String explicar(@RequestBody Map<String, Object> corpo) {

        return """
                Com base nas opções enviadas,
                a estratégia selecionou as melhores alternativas
                dentro do orçamento informado.
                """;
    }

}
