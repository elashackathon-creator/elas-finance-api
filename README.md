git checkout -b nome-da-feature

## 🛠️ Notas de Implementação (Etapa 2)

Nesta etapa, foquei em aplicar os requisitos de **Java Moderno (v21)** e **Spring Boot 3**.

### Conceitos Aplicados:
* **Sealed Interfaces & Records:** Controle rigoroso do domínio `OpcaoFinanceira`.
* **Pattern Matching for Switch:** Lógica de decisão limpa e moderna.
* **Virtual Threads:** Otimização de performance ativada via `application.properties`.
* **Jackson Polymorphism:** Tratamento de herança em APIs REST.

### Comandos Git utilizados nesta etapa:
```bash
# Adicionando as classes de domínio e lógica de otimização
git add src/main/java/com/elafinance/finopt/domain/
git add src/main/java/com/elafinance/finopt/usecase/

# Commit com as funcionalidades de Java 21
git commit -m "feat: implementa sealed types e record patterns no motor de otimização"

# Subindo as alterações para o repositório compartilhado
git push origin master