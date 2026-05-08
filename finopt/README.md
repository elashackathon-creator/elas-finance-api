# ElaFinance API

API REST de otimização financeira com foco em autonomia financeira feminina.
O sistema recebe as opções financeiras da usuária, analisa dentro do orçamento disponível usando o algoritmo da mochila e retorna um plano priorizado com análise de risco, critérios de seleção e explicação gerada por Inteligência Artificial.

---

## Funcionalidades

- Registro e autenticação de usuárias com token JWT
- Algoritmo de otimização financeira baseado no problema da mochila (maximização de retorno)
- Classificação automática de risco (BAIXO, MÉDIO, ALTO) e pontuação financeira
- Explicação dos critérios de seleção de cada opção recomendada
- Persistência do histórico de otimizações
- Assistente financeira com IA (Ela) para perguntas livres e explicação de resultados
- Documentação interativa via Swagger UI

---

## Tecnologias

| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 (LTS) |
| Framework | Spring Boot | 3.5 |
| Segurança | Spring Security + JWT (jjwt) | 0.12.6 |
| Persistência | Spring Data JPA + H2 | — |
| IA Generativa | Groq API (llama-3.3-70b-versatile) | — |
| Documentação | SpringDoc OpenAPI / Swagger UI | 2.8.3 |
| Utilitários | Lombok | — |
| Testes | JUnit 5 + Mockito | — |

**Recursos do Java 21 utilizados:**
- `record` — DTOs e objetos de domínio imutáveis
- `sealed interface` — modelagem de resultado com tipos exaustivos (`ResultadoOtimizacao`)
- Switch expressions com pattern matching
- Virtual threads (`spring.threads.virtual.enabled=true`)

---

## Arquitetura

```
com.elafinance.finopt
├── controller      → Endpoints REST, DTOs, tratamento global de erros
├── domain          → Entidades de domínio (records, sealed interfaces)
├── usecase         → Algoritmo de otimização (Strategy Pattern), JWT, IA
├── infraestrutura  → Persistência JPA, repositórios Spring Data
└── config          → Segurança JWT, Swagger, filtro de autenticação
```

**Padrões aplicados:**
- **Strategy** — `EstrategiaOtimizacao` permite trocar o algoritmo sem alterar o controller
- **Sealed Interface** — `ResultadoOtimizacao` com `ComRecomendacoes | SemOpcoes` garante tratamento exaustivo
- **Clean Architecture** — separação estrita entre camadas sem dependências circulares

---

## Como executar

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Chave de API do Groq (gratuita em [console.groq.com](https://console.groq.com))

### Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `GROQ_API_KEY` | Chave da API do Groq para o módulo de IA |

### Pelo Maven
```bash
GROQ_API_KEY=sua_chave ./mvnw spring-boot:run
```

### Pelo IntelliJ
1. Abrir `FinoptApplication.java`
2. Run → Edit Configurations → Environment Variables → adicionar `GROQ_API_KEY`
3. Executar normalmente

---

## Endpoints

### Autenticação (pública)

| Método | URL | Descrição |
|---|---|---|
| POST | `/auth/registrar` | Cria nova conta |
| POST | `/auth/login` | Retorna token JWT |

### Otimização (requer token)

| Método | URL | Descrição |
|---|---|---|
| POST | `/otimizar` | Gera plano financeiro otimizado |
| GET | `/otimizar/historico` | Lista histórico de otimizações |

### Inteligência Artificial (requer token)

| Método | URL | Descrição |
|---|---|---|
| POST | `/ia/explicar` | Pergunta livre para a assistente Ela |
| POST | `/ia/explicar-otimizacao` | Explica resultado de um plano gerado |

Todos os endpoints protegidos exigem o header:
```
Authorization: Bearer <token>
```

---

## Exemplos de uso

### 1. Registrar
```json
POST /auth/registrar
{
  "nome": "Jessica",
  "email": "jessica@email.com",
  "senha": "minhasenha"
}
```

### 2. Login
```json
POST /auth/login
{
  "email": "jessica@email.com",
  "senha": "minhasenha"
}

// Resposta
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 3. Otimizar carteira
```json
POST /otimizar
Authorization: Bearer eyJ...

{
  "orcamento": 800,
  "opcoes": [
    { "nome": "Curso de Tecnologia", "custo": 400, "retornoEsperado": 600, "categoria": "EDUCACAO" },
    { "nome": "Streaming",           "custo": 45,  "retornoEsperado": 30,  "categoria": "LAZER" },
    { "nome": "Viagem",              "custo": 3000,"retornoEsperado": 500, "categoria": "LAZER" }
  ]
}

// Resposta
{
  "mensagem": "Plano financeiro otimizado com sucesso.",
  "custoTotal": 445.0,
  "retornoTotal": 630.0,
  "analise": {
    "recomendacao": "Plano focado em crescimento: prioriza educação e investimentos.",
    "nivelRisco": "BAIXO",
    "pontuacao": 141.57
  },
  "recomendacoes": [
    { "nome": "Curso de Tecnologia", "custo": 400.0, "retornoEsperado": 600.0, "categoria": "EDUCACAO" },
    { "nome": "Streaming",           "custo": 45.0,  "retornoEsperado": 30.0,  "categoria": "LAZER" }
  ],
  "criterios": [
    "Critério 1: eliminadas opções com custo superior ao orçamento de R$800,00",
    "Critério 2: eliminadas opções com retorno esperado zero ou negativo",
    "Critério 3: opções ordenadas por maior retorno esperado (algoritmo da mochila — maximização de valor)",
    "Critério 4: selecionadas as 2 opção(ões) de maior retorno que cabem no orçamento"
  ]
}
```

### 4. Perguntar para a IA
```json
POST /ia/explicar
Authorization: Bearer eyJ...

{ "mensagem": "Como começar a investir com pouco dinheiro?" }

// Resposta
{
  "status": "sucesso",
  "analise": "Começar a investir com pouco dinheiro é totalmente possível! ..."
}
```

---

## Categorias suportadas

| Categoria | Descrição |
|---|---|
| `EDUCACAO` | Cursos, capacitações, livros |
| `INVESTIMENTO` | Aplicações financeiras, ações |
| `SAUDE` | Planos, medicamentos, terapia |
| `ALIMENTACAO` | Gastos com alimentação |
| `LAZER` | Entretenimento, viagens, assinaturas |

---

## Documentação interativa

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| H2 Console | `http://localhost:8080/h2-console` |

H2 Console — JDBC URL: `jdbc:h2:mem:elafinancedb` / usuário: `sa` / senha: *(vazia)*

---

## Testes

```bash
./mvnw test
```

- `ControleRecomendacaoTest` — testes de unidade do controller com Mockito
- `FinoptApplicationTests` — testes de integração com contexto completo Spring Boot

---

## Segurança

- Senhas armazenadas com hash **BCrypt**
- Autenticação **stateless** via JWT (sem sessão no servidor)
- Token com validade de **24 horas**
- Rotas `/auth/**`, `/swagger-ui/**` e `/h2-console/**` são públicas
- Todas as demais rotas exigem token válido no header `Authorization`

---

*ElaFinance — porque autonomia financeira também é liberdade.*
