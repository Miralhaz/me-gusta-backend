## Context

Estado atual e restrições que moldam a abordagem (motivação e escopo: ver `proposal.md` — Why):

- `VendasService` concentra hoje três responsabilidades: (1) orquestração da importação (`importarPlanilha`/`lerPlanilha`, transacional), (2) extração dependente do formato (`detectarFormato` + `enum FormatoPlanilha` + 3 leitores com nomes de coluna/aba hard-coded) e (3) baixa de estoque (`baixarEstoque`). A extração é o único ponto acoplado aos três layouts.
- O pipeline downstream (`baixarEstoque`) é estável e testado: resolução de usuário JWT, motivo `Venda` (cria se ausente), `findByNomeIgnoreCase` na fogazza, INNER JOIN `fogazza_insumo`, acumulação `quantidade_insumo × unidades`, débito de `insumo.qtd_atual` + recálculo de status via `TipoStatusService`, INSERT em `saida_estoque`, rollback atômico, itens sem fogazza ignorados. **Este pipeline não muda.**
- Normalização comum: `ItemVendido(nomeItem, quantidade)` + `agregarPorNome` (soma por nome, case-sensitive).
- As três fixtures de regressão estão em `megusta/src/test/resources/` (`historico_itens_vendidos.xlsx`, `pedidos_recentes.xlsx`, `relatorio_cardapio.xlsx`) e reproduzem os exports reais fornecidos (colunas fictícias numéricas `xxx` presentes).
- Requisitos de comportamento: ver `specs/importacao-vendas/spec.md`.

## Goals / Non-Goals

**Goals:**
- Extração **genérica e auto-adaptativa**: qualquer workbook `.xlsx` com colunas de nome de item e de quantidade é aceito, sem enum, sem `switch` de formato e sem nomes de coluna/aba fixos no fluxo.
- Separation of concerns: a extração heurística fica em um componente próprio (testável isoladamente, sem dependência de banco); `VendasService` mantém a orquestração e a baixa de estoque intactas.
- As três fixtures atuais continuam produzindo exatamente os mesmos itens (regressão).

**Non-Goals:**
- Não alterar o pipeline de baixa de estoque (nenhuma mudança em entidades, repositórios, transação ou persistência).
- Sem configuração externa de layouts (propriedades/DB) — decisão do usuário: parser auto-adaptativo.
- Sem normalização de nomes (acentos/sufixos) entre planilha e `fogazza.nome` — permanece fora de escopo (item sem fogazza é ignorado).
- Sem alterações de banco (`megustaV06.sql` intacto).

## Decisions

### 1. Novo componente `PlanilhaVendasExtractor` (extração pura, sem banco)
Extrair a lógica de leitura para uma classe própria (Spring `@Component`) em `service`, com um único método público que recebe um `Workbook` (ou `InputStream` aberto pelo chamador) e devolve `List<ItemVendido>` brutas. `VendasService` passa a:
- `lerPlanilha(MultipartFile)`: abre o `Workbook` e delega ao extrator; depois `importarPlanilha` agrega (`ItemVendido.agregarPorNome`) e chama `baixarEstoque`.
- `baixarEstoque`: **inalterado**.

*Alternativa considerada:* manter tudo em `VendasService` (menos movimento), como hoje. Rejeitada: `VendasService` já mistura três responsabilidades; a heurística de colunas é a parte nova e mais arriscada e merece testes unitários diretos sem `@InjectMocks` das 6 dependências de repositório.

### 2. Heurística de identificação de colunas (por aba, cabeçalho na linha 0)
Para cada aba do workbook, com base no cabeçalho (linha `0`) e nas demais linhas:

- **Coluna de nome**: pontua cada coluna pela quantidade de células textuais (STRING, não vazias) no corpo. Bônus de alias quando o cabeçalho contém termos de item/produto (ex.: "Item", "Nome", "Prod"). A coluna com maior pontuação textual vira coluna de nome. Colunas de cabeçalho numérico não concorrem (nada textual no corpo).
- **Coluna de quantidade**: pontua cada coluna pela quantidade de células numéricas no corpo, com **bônus forte de alias** quando o cabeçalho contém termos de quantidade ("Qtd.", "Quant", "Vendas total", "Vendas", "quantidade") e **penalidade** para termos de valor/preço ("Valor", "Total", "Preço", "Ganhos", "R$"). Isso garante que em fixtures como o histórico ("Qtd." × "Valor Un. Item") e o relatório de cardápio ("Vendas total (quantidade)" × "Visitas"/"Pedidos"/"Valor Total") a coluna correta vença.
- **Células `;`-separadas**: se a coluna de nome escolhida contiver células com `;`, o modo de contagem é **1 unidade por ocorrência** (como no layout de pedidos recentes) e **não** se aplica coluna de quantidade. Evita que colunas numéricas irrelevantes da aba (ex.: número do pedido, valores pagos) sejam interpretadas como quantidade.
- **Aba sem coluna de nome com corpo textual ou sem linhas válidas** → aba ignorada (cobre "Funil Loja", abas only-numeric etc.).

*Alternativas consideradas:* (a) buscar no banco os nomes de fogazza para casar a coluna de item — rejeitada para manter o extrator livre de banco e determinístico; (b) tabela de aliases exaustiva — rejeitada porque reintroduz acoplamento a layouts conhecidos (o usuário optou por parser auto-adaptativo); os bônus de alias são apenas *desempate* heurístico, não gates de formato.

### 3. Comportamento sem colunas identificáveis
Se nenhuma aba produzir pares válidos, a extração devolve lista vazia; `importarPlanilha` completa a transação sem baixas e o controller responde `200 OK` com `[]` (nada é persistido). Aplica o cenário "Planilha sem colunas de item/quantidade" do spec.

### 4. Parsing e normalização reutilizados
`valorTexto`/`valorQuantidade`/`DataFormatter` (lógica atual de leitura de células, incluindo "Qtd." textual com vírgula → `BigDecimal`) migram para o extrator sem mudança de semântica: linhas com nome em branco, quantidade não numérica, zero ou negativa são ignoradas. `ItemVendido.agregarPorNome` continua fazendo a soma final (mesmo nome em abas diferentes é somado, cobrindo o relatório de cardápio).

### 5. Controller e contrato HTTP inalterados
`VendasController` mantém validação (vazio/não-`.xlsx` → `400`, sem persistir), delega a `importarPlanilha` e retorna `200 OK` com o JSON dos itens. Apenas a descrição Swagger do endpoint pode ser ajustada para refletir "qualquer layout".

## Risks / Trade-offs

- **Heurística escolher coluna errada** (ex.: coluna de preço no lugar de quantidade, ou coluna "Pedidos"/"Visitas" no relatório de cardápio) → Mitigação: bônus/penalidade por alias de cabeçalho + contagem de células; as três fixtures são testes de regressão obrigatórios; se a plataforma introduzir cabeçalhos novos ambíguos, ajusta-se apenas o *scoring* (sem mudar spec nem pipeline).
- **Aba com coluna de nome `;`-separada E coluna de quantidade legítima**: a regra "presença de `;` ⇒ contagem por ocorrência" pode ignorar uma quantidade explícita → Trade-off aceito e documentado no spec (célula `;` conta 1 por ocorrência), consistente com o layout de pedidos recentes existente.
- **Cabeçalho que não está na linha 0** (export futuro com título/linhas de padding) → não suportado nesta iteração; assumption registrada; caso apareça, o extrator pode ganhar "busca de linha de cabeçalho" sem mudar spec.
- **Falsa sensação de cobertura de nomes**: nomes divergentes da planilha vs. `fogazza.nome` continuam sendo ignorados silenciosamente (comportamento já definido e aceito no change anterior).
- **Desempenho**: varredura completa de todas as abas/células em planilhas grandes → passa única e linear; aceitável para exports de vendas de loja única.

## Migration Plan

- **Deploy:** nenhuma migração de schema. Mudanças 100% em código Java + testes: novo `PlanilhaVendasExtractor`, `VendasService` sem enum/leitores, controller intocado. Rodar `./mvnw test` e `./mvnw verify` (JaCoCo).
- **Rollback:** reverter o commit; a importação volta ao comportamento de três formatos do change anterior. Baixas já persistidas não são desfeitas automaticamente (auditáveis em `saida_estoque`).
- **Ordem de arquivamento:** o change anterior `importacao-vendas-baixa-estoque` (não arquivado) define a capacidade `importacao-vendas` em delta; convém arquivá-lo antes deste para o archive deste change fundir o MODIFIED sobre a spec principal.

## Open Questions

- Ampliar o dicionário de aliases de cabeçalho (novos termos de "quantidade") pode ser feito depois, sem alterar spec/abordagem/tarefas — os bônus são apenas desempate.
- Suporte a cabeçalho fora da linha 0 ou linhas de título — pode ser resolvido depois, sem alterar spec/abordagem/tarefas.