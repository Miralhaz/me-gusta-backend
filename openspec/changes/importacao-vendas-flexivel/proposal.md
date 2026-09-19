## Why

`POST /vendas/importar` hoje reconhece exatamente três layouts de planilha: um `enum FormatoPlanilha` fixo + `switch` em `VendasService`, com três leitores codificados que dependem de nomes específicos de coluna/aba ("Nome Prod", "Qtd.", "Itens", "Nome do item", "Vendas total (quantidade)"…). Qualquer exportação da plataforma com layout diferente (colunas renomeadas, abas extras, novos tipos de arquivo) é rejeitada com "Formato de planilha de vendas não suportado". O negócio trabalha com exportações cujo layout varia; a importação precisa aceitar qualquer planilha que contenha nomes de itens e quantidades vendidas, sem exigir código novo por formato.

## What Changes

- **Remover** o `enum FormatoPlanilha`, o `switch` e os três leitores codificados (histórico de itens, pedidos recentes e relatório de cardápio) de `VendasService`.
- **Substituir por um extrator genérico auto-adaptativo** que:
  - percorre **todas as abas** do workbook `.xlsx`;
  - em cada aba, identifica por heurística (cabeçalho + conteúdo das células) a **coluna de nome do item** (valores textuais) e a **coluna de quantidade** (valores numéricos);
  - suporta células com **múltiplos itens separados por `;`** (1 unidade por ocorrência);
  - **descarta abas sem colunas de item/quantidade** (ex.: "Funil Loja") em vez de falhar;
  - normaliza tudo para o modelo interno `ItemVendido(nomeItem, quantidade)` agregado por nome.
- **Preservar integralmente a lógica central** já implementada: validação de 400 para arquivo vazio/não-`.xlsx`; busca da fogazza por **nome exato case-insensitive**; **INNER JOIN** em `fogazza_insumo`; acumulação por insumo (`quantidade_insumo × unidades vendidas`); baixa **transacional** de `insumo.qtd_atual` + recálculo do status via `TipoStatusService` + INSERT em `saida_estoque` (usuário autenticado via JWT e motivo `Venda` criado se ausente); **rollback atômico**; itens sem fogazza cadastrada **ignorados** sem interromper a importação; resposta `200 OK` com o JSON dos itens extraídos.
- As **três planilhas fornecidas continuam sendo aceitas** e passam a ser as *fixtures de regressão* do extrator genérico (dados fictícios com colunas `xxx` numéricas permanecem ignorados por construção).
- **Sem alterações no banco de dados** (`megustaV06.sql` permanece intacto).

## Capabilities

### New Capabilities

- Nenhuma — a mudança refina a capacidade existente declarada no change anterior (`importacao-vendas-baixa-estoque`).

### Modified Capabilities

- `importacao-vendas`: o requisito de "reconhecer três formatos fixos de planilha" é substituído por **extração genérica auto-adaptativa** de qualquer planilha `.xlsx` que contenha colunas de nome de item e quantidade (qualquer número de abas, células com `;`, abas sem dados de venda ignoradas). Todo o pipeline downstream (agregação, baixa transacional de estoque, saídas, usuário/motivo, resposta JSON) permanece como definido.

## Impact

- `megusta/src/main/java/school/sptech/megusta/service/VendasService.java` — refatoração central: remoção do enum/`switch`/leitores de formato e inserção do extrator genérico (detecção heurística de colunas por aba).
- `megusta/src/main/java/school/sptech/megusta/controller/VendasController.java` — sem mudança funcional (continua fino); apenas documentação Swagger, se necessário.
- `megusta/src/main/java/school/sptech/megusta/dto/planilha_vendas/ItemVendido.java` — reutilizado sem alteração (agregação por nome preservada).
- Suporte (novo, sem alterar banco): classes auxiliares de extração de colunas/heurística em `package` de vendas, se o design assim decidir.
- Testes: ajuste de `VendasServiceTest` (remoção de `detectarFormato`, novos casos para layouts arbitrários/renomeados/multi-aba) e `VendasControllerTest` (inalterado ou ajuste mínimo); fixtures atuais mantidas em `src/test/resources`.
- Banco de dados: **sem alterações** (`megustaV06.sql` de referência).