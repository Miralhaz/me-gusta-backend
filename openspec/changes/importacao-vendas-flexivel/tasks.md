## 1. Novo componente de extração genérica (PlanilhaVendasExtractor)

- [x] 1.1 Criar `PlanilhaVendasExtractor` (`@Component`) em `megusta/src/main/java/school/sptech/megusta/service/` com método público que recebe o `Workbook` e devolve `List<ItemVendido>`; migrar para ele os helpers de leitura de células (`valorTexto`, `valorQuantidade`, `DataFormatter`, incluindo "Qtd." textual com vírgula → `BigDecimal`) — verificar que o projeto compila (`./mvnw -q compile`)
- [x] 1.2 Implementar a heurística de identificação de colunas por aba (cabeçalho na linha 0): coluna de **nome** pontuada por células textuais no corpo (+ bônus de alias "item/nome/prod"); coluna de **quantidade** pontuada por células numéricas (+ bônus forte de alias "qtd./quant/vendas/quantidade" e penalidade para "valor/preço/total/ganhos/r$") — verificar com teste unitário que as colunas corretas são escolhidas em cada aba das três fixtures
- [x] 1.3 Implementar a extração por aba: pares (nome, quantidade) com linhas inválidas ignoradas (nome em branco, quantidade não numérica/zero/negativa) e modo `;`-separado (1 unidade por ocorrência, sem aplicar coluna de quantidade) quando a coluna de nome contiver células com `;` — verificar com teste unitário
- [x] 1.4 Ignorar abas sem coluna de nome com corpo textual (ex.: "Funil Loja") e retornar lista vazia quando nenhuma aba for válida — verificar com teste unitário

## 2. Refatoração do VendasService

- [x] 2.1 Remover de `VendasService` o `enum FormatoPlanilha`, `detectarFormato` e os leitores fixos (`lerHistoricoItensVendidos`, `lerPedidosRecentes`, `lerRelatorioCardapio`, `lerAbaRelatorioCardapio`) — verificar que o projeto compila
- [x] 2.2 `lerPlanilha` passa a abrir o `Workbook` e delegar a extração ao `PlanilhaVendasExtractor`; remover `processarWorkbook`; `importarPlanilha` mantém agregação (`ItemVendido.agregarPorNome`) + `baixarEstoque` @Transactional — verificar que compila e os testes ajustados passam
- [x] 2.3 Preservar `baixarEstoque` sem alterações (usuário JWT, motivo `Venda`, INNER JOIN `fogazza_insumo`, acumulação, débito + status, INSERT `saida_estoque`, rollback, itens sem fogazza ignorados) — verificar que os testes existentes de baixa passam sem modificação
- [x] 2.4 Ajustar a documentação Swagger no `VendasController` (se necessário) para refletir a extração de qualquer layout de planilha — verificar que o endpoint mantém 400 (vazio/não-.xlsx) e 200 (JSON dos itens)

## 3. Testes

- [x] 3.1 Criar `PlanilhaVendasExtractorTest` cobrindo: as três fixtures produzem os mesmos itens de antes (regressão: histórico 1 item; pedidos recentes 1 unidade por ocorrência; relatório de cardápio somando abas `Itens`/`Complementos` e ignorando `Funil Loja`); layout desconhecido com colunas identificáveis; colunas renomeadas/reordenadas; workbook multi-abas com aba dashboard; célula `;`; mesmo item somado entre abas; planilha sem colunas → lista vazia — verificar `./mvnw test`
- [x] 3.2 Ajustar `VendasServiceTest`: remover testes de `detectarFormato`/enum e adaptar `lerPlanilha` ao novo extrator; manter os testes de baixa de estoque (orquestração, usuário, motivo, itens sem fogazza, rollback, estoque insuficiente) — verificar `./mvnw test`
- [x] 3.3 Confirmar que `VendasControllerTest` continua válido (400 para vazio/não-.xlsx sem persistir; 200 com itens extraídos) — criar um teste extra apenas se o controller mudar — verificar `./mvnw test`

## 4. Verificação final

- [x] 4.1 Executar `./mvnw test` e `./mvnw verify` (JaCoCo) — verificar que todos os testes passam sem regressões
- [ ] 4.2 (Manual, opcional) Subir a aplicação e importar as três planilhas fornecidas via Swagger — verificar `200 OK` e a baixa de estoque esperada