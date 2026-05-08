# ElaFinance API

Sistema de otimização financeira com foco em autonomia financeira feminina.

## Tecnologias

- Java 21 (Virtual Threads)
- Spring Boot 3.5
- Spring Data JPA + H2
- Spring Security
- SpringDoc OpenAPI / Swagger UI

## Como executar

### Pré-requisitos
- Java 21+
- Maven 3.8+

### Executar pelo Maven
```bash
./mvnw spring-boot:run
```

### Executar pelo IntelliJ
Abrir `FinoptApplication.java` → botão direito → Run

## Endpoints

| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/otimizar` | Otimiza a carteira financeira |
| GET | `/otimizar/historico` | Histórico de otimizações salvas |
| POST | `/ia/explicar` | Análise com IA |

## Documentação

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:elafinancedb`)

## Exemplos de uso

### POST /otimizar
```json
{
  "orcamento": 500,
  "opcoes": [
    {"nome": "Curso de Tecnologia", "custo": 400, "retornoEsperado": 350, "categoria": "EDUCACAO"},
    {"nome": "Cancelar Streaming", "custo": 50, "retornoEsperado": 80, "categoria": "LAZER"},
    {"nome": "Viagem cara", "custo": 3000, "retornoEsperado": 100, "categoria": "LAZER"}
  ]
}
```

### POST /ia/explicar
```json
{
  "mensagem": "Quero melhorar minha organização financeira"
}
```

## Arquitetura

```
com.elafinance.finopt
├── controller      → Endpoints REST, DTOs, tratamento de erros
├── domain          → Entidades e regras de negócio puras
├── usecase         → Algoritmo de otimização (Strategy Pattern)
├── infraestrutura  → Persistência JPA, repositórios
└── config          → Segurança, Swagger
```

## Design Patterns aplicados

- **Strategy**: `EstrategiaOtimizacao` permite trocar o algoritmo de otimização
- **Builder**: `HistoricoOtimizacao` usa Lombok `@Builder` para construção segura
