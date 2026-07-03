# contas-domesticas-api

API backend do projeto **Contas Domésticas**.

## Propósito

O **Contas Domésticas** é um app de uso próprio (família) que unifica, num só produto:

- **Finanças familiar e individual** — receitas, despesas e investimentos separados por **carteira**
  (compartilhada ou privada), com recorrência, parcelamento e **rateio** entre as pessoas.
- **Listas de compras** — mantimentos e material de construção, com itens por **unidade, peso (kg) ou
  volume (L)**, preço estimado × real e **histórico de preço** por mercado; ao fechar, a lista vira
  uma despesa.
- **Investimentos** — aportes, evolução patrimonial e reserva de emergência.
- **Calculadoras** — investimento, IR sobre investimentos, financiamento (Price/SAC) e preço por
  unidade.
- **Configuração parametrizável** — índices (Selic, CDI, IPCA) e alíquotas (IR/IOF).

Esta **API é local** e cumpre dois papéis: expor o domínio (CRUDs, relatórios) e servir de ponto de
**sincronização** entre os celulares (conflitos resolvidos pelo registro mais recente). Já
implementado: **Usuário** + **Auditoria** (log automático de cada requisição). O roadmap completo está
em `PLANO-api.md` (raiz do workspace).

## Stack

- Java 17
- Spring Boot 3.5.16
- Spring Web / Spring Data JPA
- Spring Security + JWT (jjwt) — infraestrutura (config/filtros na próxima rodada)
- PostgreSQL (driver) — conexão local
- Flyway (migrações de banco)
- Jakarta Bean Validation
- Spring Boot Actuator (health/info com build + git)
- Jackson (suporte a `java.time` via jsr310)
- springdoc-openapi / Swagger UI
- MapStruct / Lombok
- H2 (testes em memória) + spring-security-test
- JaCoCo (cobertura) · DevTools (dev)
- Maven

## Profiles

| Profile | Banco | Uso |
|---------|-------|-----|
| `dev` (default) | PostgreSQL local | desenvolvimento |
| `test` | H2 em memória | testes (forçado pelo surefire) |

## Testes & cobertura

```bash
./mvnw verify   # roda no profile test (H2) e gera JaCoCo em target/site/jacoco/
```

CI: GitHub Actions (`.github/workflows/ci.yml`) roda `verify` a cada push/PR.

## Banco de dados

Conexão local configurada em `application.yml`:

| Parâmetro | Valor |
|-----------|-------|
| host/porta | `localhost:5432` |
| database | `contasdomesticas` |
| usuário | `contasdomesticas` |
| senha | `contasapi` |

Os scripts de criação de role/database ficam no repositório `contas-domesticas-db`.
Migrações Flyway em `src/main/resources/db/migration` (`V1__*.sql`, ...) — **fonte de verdade** do
schema, espelhada no repo `contas-domesticas-db`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator health: `http://localhost:8080/actuator/health`

## Estrutura

```
src/main/java/br/com/contasdomesticas/api/   # domain, repository, dto, mapper, service, controller, config, audit, exception
src/main/resources/application.yml           # configuração (+ db/migration Flyway)
src/test/java/br/com/contasdomesticas/api/   # testes (repository, service, integração)
```

## Executar

```bash
./mvnw spring-boot:run
```

## Build

```bash
./mvnw clean package
```

## Documentação

- Roadmap de desenvolvimento: `PLANO-api.md` (raiz do workspace).
- Tarefas (modelo ClickUp): `contas-domesticas-documentacao/sprint-1/api/`.
