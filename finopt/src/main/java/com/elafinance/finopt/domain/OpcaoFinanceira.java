package com.elafinance.finopt.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "categoria", // O Spring vai olhar esse campo no JSON
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Investimento.class, name = "Investimento"),
        @JsonSubTypes.Type(value = CorteDeGasto.class, name = "Corte de Gasto")
})

public sealed interface OpcaoFinanceira permits Investimento, CorteDeGasto {
    String nome();
    double custo();
    double retornoEsperado();
    String categoria();
}
