# contas-domesticas-api

API backend do projeto **Contas Domésticas**.

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
Migrações Flyway em `src/main/resources/db/migration` (`V1__*.sql`, ...).

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator health: `http://localhost:8080/actuator/health`

## Estrutura

```
src/main/java/br/com/contasdomesticas/api/   # código-fonte
src/main/resources/application.yml           # configuração
src/test/java/br/com/contasdomesticas/api/   # testes
```

## Executar

```bash
./mvnw spring-boot:run
```

## Build

```bash
./mvnw clean package
```
