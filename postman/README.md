# Postman — Contas Domesticas API

Colecao completa da API (JWT + todos os recursos), pronta para testar a API isoladamente.

## Arquivos

- `contas-domesticas.postman_collection.json` — 18 pastas, ~100 requisicoes.
- `contas-domesticas.postman_environment.json` — environment local (`baseUrl`, credenciais).

## Como usar

1. **Importe** os dois arquivos no Postman (Import) e selecione o environment
   *"Contas Domesticas - Local"* (ou use as variaveis ja embutidas na colecao).
2. Suba a API (`./mvnw spring-boot:run`, profile `dev`, em `http://localhost:8080`).
3. Rode **`Auth > Login`** primeiro — ele autentica com `admin/admin` e **salva o
   `accessToken`** na colecao. Todas as demais requisicoes ja mandam `Authorization: Bearer`.
4. As requisicoes de **Criar** salvam os IDs em variaveis (`carteiraId`, `categoriaDespesaId`,
   `produtoId`, `mercadoId`, `unidadeId`, `listaId`, `itemId`, `investimentoId`, ...), entao o
   fluxo encadeia sozinho.

### Fluxo sugerido (ponta a ponta)

Auth > Login → Carteiras > Criar (familiar) → Categorias > Criar (receita) + Criar (despesa) →
Unidades > Criar → Mercados > Criar → Produtos > Criar + Adicionar cotacao →
Receitas > Criar → Despesas > Criar (+ Pagar / Parceladas / Rateio) →
Compras > Criar lista → Adicionar item → Escolher estabelecimento → Fechar →
Investimentos > Criar + Aporte → Saldo/Patrimonio →
Relatorios > Saldo do mes / Por categoria → Sincronizacao > Delta/Merge.

## Variaveis principais

| Variavel | Uso |
|----------|-----|
| `baseUrl` | `http://localhost:8080` |
| `accessToken` / `refreshToken` | preenchidas pelo Login/Refresh |
| `adminLogin` / `adminSenha` | `admin` / `admin` |
| `*Id` | preenchidas pelas requisicoes de criacao |

> A colecao e regenerada por `scripts` de build; ao adicionar endpoints novos, atualize-a.
