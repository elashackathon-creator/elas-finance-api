# ElaFinance — Documentação de Entrega
**Hackathon Artemisia Elas+ Tech | Trilha Back-end**

---

## Visão Geral

O **ElaFinance** é uma API REST para otimização financeira pessoal com foco em autonomia financeira feminina. A plataforma recebe as opções financeiras da usuária (cursos, investimentos, gastos), analisa dentro do orçamento disponível e retorna um plano priorizado com análise de risco e explicação gerada por IA.

---

## User Stories

| ID  | Como...         | Quero...                                              | Para...                                                    | Prioridade |
|-----|-----------------|-------------------------------------------------------|------------------------------------------------------------|------------|
| US1 | usuária nova    | me cadastrar com nome, e-mail e senha                 | criar minha conta com segurança                            | Alta       |
| US2 | usuária         | fazer login e receber um token                        | acessar as funcionalidades protegidas da API               | Alta       |
| US3 | usuária         | informar meu orçamento e lista de opções financeiras  | receber um plano otimizado dentro do que posso gastar      | Alta       |
| US4 | usuária         | receber uma pontuação e classificação de risco        | entender o nível de saúde do meu plano financeiro          | Alta       |
| US5 | usuária         | consultar o histórico das minhas otimizações          | acompanhar minha evolução financeira ao longo do tempo     | Média      |
| US6 | usuária         | receber uma explicação da IA sobre minhas escolhas    | entender em linguagem simples por que aquelas opções foram recomendadas | Média |
| US7 | usuária         | fazer perguntas livres sobre finanças para a Ela      | tirar dúvidas sobre educação financeira de forma acolhedora | Média     |
| US8 | desenvolvedora  | acessar a documentação interativa da API              | testar os endpoints sem configuração adicional             | Baixa      |

---

## Backlog Priorizado

### Sprint 1 — Núcleo da API
- [x] Estrutura do projeto com Clean Architecture
- [x] Domínio: `OpcaoFinanceira`, `ResultadoOtimizacao` (sealed interface), `AnaliseFinanceira`
- [x] Estratégia de otimização (`EstrategiaOtimizacao` + `OtimizacaoSimples`)
- [x] Endpoint `POST /otimizar` com validação de entrada
- [x] Classificação de risco e pontuação financeira
- [x] Tratamento global de erros (`TratadorDeErros`)

### Sprint 2 — Persistência e Documentação
- [x] Entidade JPA `HistoricoOtimizacao` + repositório
- [x] Persistência automática de cada otimização no banco H2
- [x] Endpoint `GET /otimizar/historico`
- [x] Swagger UI via SpringDoc OpenAPI
- [x] Testes unitários e de integração com Mockito

### Sprint 3 — Segurança
- [x] Entidade `Usuario` com Spring Security (`UserDetails`)
- [x] Endpoint `POST /auth/registrar`
- [x] Endpoint `POST /auth/login` com retorno de token JWT
- [x] Filtro JWT (`FiltroJWT`) para autenticação stateless
- [x] Proteção de rotas: `/otimizar/**` e `/ia/**` exigem token
- [x] Senhas com hash BCrypt

### Sprint 4 — Inteligência Artificial
- [x] Integração com Google Gemini 2.0 Flash via REST
- [x] Endpoint `POST /ia/explicar` — perguntas livres para a Ela
- [x] Endpoint `POST /ia/explicar-otimizacao` — explicação do resultado do plano

---

## Arquitetura

```
┌──────────────────────────────────────────────────────┐
│                    Cliente HTTP                       │
│             (Postman / App Frontend)                  │
└─────────────────────┬────────────────────────────────┘
                      │
              Authorization: Bearer <token>
                      │
┌─────────────────────▼────────────────────────────────┐
│                  Spring Boot API                      │
│                                                       │
│  ┌──────────────────────────────────────────────┐    │
│  │              Controller Layer                │    │
│  │  /auth  /otimizar  /ia                       │    │
│  └──────────────────┬─────────────────────────  ┘    │
│                     │                                 │
│  ┌──────────────────▼─────────────────────────  ┐    │
│  │              Use Case Layer                  │    │
│  │  EstrategiaOtimizacao  ServicoJWT            │    │
│  │  ServicoGemini         ServicoAutenticacao   │    │
│  └──────────────────┬─────────────────────────  ┘    │
│                     │                                 │
│  ┌──────────────────▼─────────────────────────  ┐    │
│  │               Domain Layer                   │    │
│  │  OpcaoFinanceira   ResultadoOtimizacao        │    │
│  │  AnaliseFinanceira Usuario                   │    │
│  └──────────────────┬─────────────────────────  ┘    │
│                     │                                 │
│  ┌──────────────────▼─────────────────────────  ┐    │
│  │           Infrastructure Layer               │    │
│  │  HistoricoOtimizacaoRepositorio              │    │
│  │  UsuarioRepositorio  (Spring Data JPA)       │    │
│  └──────────────────────────────────────────────┘    │
│                                                       │
│  ┌────────────────────────────────────────────  ┐    │
│  │          Security Layer (transversal)        │    │
│  │  FiltroJWT  ConfiguracaoSeguranca  BCrypt    │    │
│  └──────────────────────────────────────────────┘    │
└──────────────────┬────────────────────┬──────────────┘
                   │                    │
        ┌──────────▼──────┐   ┌────────▼──────────────┐
        │   H2 Database   │   │   Google Gemini API    │
        │  (in-memory)    │   │   gemini-2.0-flash     │
        └─────────────────┘   └───────────────────────┘
```

**Padrões aplicados:**
- **Clean Architecture** — separação em camadas controller / usecase / domain / infraestrutura
- **Strategy Pattern** — `EstrategiaOtimizacao` permite trocar o algoritmo de otimização sem alterar o controller
- **Sealed Interface** — `ResultadoOtimizacao` com `ComRecomendacoes | SemOpcoes` (Java 21)

---

## Modelo ER

```
┌──────────────────────────────┐     ┌──────────────────────────────────────┐
│           usuarios           │     │         historico_otimizacao          │
├──────────────────────────────┤     ├──────────────────────────────────────┤
│ id            BIGINT (PK)    │     │ id                  BIGINT (PK)       │
│ nome          VARCHAR        │     │ data_hora           TIMESTAMP         │
│ email         VARCHAR UNIQUE │     │ orcamento           DOUBLE            │
│ senha         VARCHAR        │     │ custo_total         DOUBLE            │
└──────────────────────────────┘     │ retorno_total       DOUBLE            │
                                     │ pontuacao           DOUBLE            │
                                     │ nivel_risco         VARCHAR           │
                                     │ quantidade_recs     INTEGER           │
                                     └──────────────────────────────────────┘
```

> Banco H2 em memória para desenvolvimento. Em produção, substituir pela URL de um banco relacional (PostgreSQL, MySQL) via variável de ambiente `spring.datasource.url`.

---

## Stack Tecnológica

| Camada         | Tecnologia                              | Versão     |
|----------------|-----------------------------------------|------------|
| Linguagem      | Java                                    | 21 (LTS)   |
| Framework      | Spring Boot                             | 3.5.14     |
| Segurança      | Spring Security + JWT (jjwt)            | 0.12.6     |
| Persistência   | Spring Data JPA + Hibernate             | 6.6        |
| Banco de dados | H2 (in-memory)                          | 2.3        |
| IA Generativa  | Google Gemini 2.0 Flash (via REST)      | —          |
| Documentação   | SpringDoc OpenAPI (Swagger UI)          | 2.8.3      |
| Utilitários    | Lombok                                  | —          |
| Testes         | JUnit 5 + Mockito + Spring Test         | —          |

**Recursos do Java 21 utilizados:**
- `record` — DTOs e objetos de domínio imutáveis (`OpcaoFinanceira`, `AnaliseFinanceira`, `RespostaOtimizacao`)
- `sealed interface` — modelagem de resultado com tipos exaustivos (`ResultadoOtimizacao`)
- Switch expressions com pattern matching — despacho por tipo no controller
- Virtual threads (`spring.threads.virtual.enabled=true`) — maior throughput sem configuração extra

---

## Contrato da API

### Autenticação

| Método | Endpoint          | Autenticação | Descrição           |
|--------|-------------------|--------------|---------------------|
| POST   | `/auth/registrar` | Pública      | Cria nova usuária   |
| POST   | `/auth/login`     | Pública      | Retorna token JWT   |

**POST /auth/registrar**
```json
// Request
{ "nome": "Jessica", "email": "jessica@email.com", "senha": "minhasenha" }

// Response 201
{ "mensagem": "Cadastro realizado com sucesso." }
```

**POST /auth/login**
```json
// Request
{ "email": "jessica@email.com", "senha": "minhasenha" }

// Response 200
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

---

### Otimização Financeira

| Método | Endpoint              | Autenticação | Descrição                          |
|--------|-----------------------|--------------|-------------------------------------|
| POST   | `/otimizar`           | Bearer token | Gera plano financeiro otimizado    |
| GET    | `/otimizar/historico` | Bearer token | Lista histórico de otimizações     |

**POST /otimizar**
```json
// Request
{
  "orcamento": 800,
  "opcoes": [
    { "nome": "Curso de Excel", "custo": 150, "retornoEsperado": 400, "categoria": "EDUCACAO" },
    { "nome": "Streaming",      "custo": 45,  "retornoEsperado": 30,  "categoria": "LAZER" },
    { "nome": "Viagem",         "custo": 2000,"retornoEsperado": 500, "categoria": "LAZER" }
  ]
}

// Response 200
{
  "mensagem": "Plano financeiro otimizado com sucesso.",
  "custoTotal": 195.0,
  "retornoTotal": 430.0,
  "analise": {
    "recomendacao": "Plano equilibrado com bom retorno esperado.",
    "nivelRisco": "BAIXO",
    "pontuacao": 87.5
  },
  "recomendacoes": [
    { "nome": "Curso de Excel", "custo": 150.0, "retornoEsperado": 400.0, "categoria": "EDUCACAO" },
    { "nome": "Streaming",      "custo": 45.0,  "retornoEsperado": 30.0,  "categoria": "LAZER" }
  ]
}
```

---

### Inteligência Artificial

| Método | Endpoint                   | Autenticação | Descrição                              |
|--------|----------------------------|--------------|----------------------------------------|
| POST   | `/ia/explicar`             | Bearer token | Pergunta livre para a assistente Ela   |
| POST   | `/ia/explicar-otimizacao`  | Bearer token | Explica resultado do plano em IA       |

**POST /ia/explicar**
```json
// Request
{ "mensagem": "Como devo começar a investir com pouco dinheiro?" }

// Response 200
{
  "status": "sucesso",
  "analise": "Ótima pergunta! Começar a investir com pouco é totalmente possível..."
}
```

---

## Como Executar

```bash
# 1. Clone o repositório
git clone <url-do-repo>

# 2. Configure a chave do Gemini (IntelliJ: Run > Edit Configurations > Environment Variables)
GEMINI_API_KEY=sua_chave_aqui

# 3. Execute
./mvnw spring-boot:run

# 4. Acesse a documentação interativa
http://localhost:8080/swagger-ui/index.html
```

---

## Testes

```bash
./mvnw test
```

- `ControleRecomendacaoTest` — testes de unidade do controller com Mockito (`@WebMvcTest`)
- `FinoptApplicationTests` — testes de integração com contexto completo (`@SpringBootTest`)
- Cobertura: validação de entrada, otimização com e sem resultados, histórico, autenticação, IA

---

*ElaFinance — porque autonomia financeira também é liberdade.*
