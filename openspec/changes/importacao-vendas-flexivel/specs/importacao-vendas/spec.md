## MODIFIED Requirements

### Requirement: Reconhecer o formato da planilha de vendas
O sistema SHALL aceitar **qualquer** planilha `.xlsx` que contenha nomes de itens e quantidades vendidas e extrair dela os pares (nome do item, quantidade vendida), **sem depender de um conjunto fixo de layouts conhecidos**:

- O sistema SHALL percorrer todas as abas do workbook e identificar, em cada aba, a coluna de nome do item (conteúdo textual) e a coluna de quantidade (conteúdo numérico), por inspeção heurística do cabeçalho e dos valores das células.
- Abas **sem** colunas identificáveis de item/quantidade SHALL ser ignoradas (ex.: abas de dashboard), sem tratar como erro.
- Células com **vários itens separados por `;`** SHALL ser interpretadas como 1 unidade por ocorrência de cada nome.
- O mesmo nome de item em abas diferentes SHALL ter suas quantidades somadas.
- O sistema SHALL continuar extraindo corretamente os três layouts anteriormente suportados (histórico de itens vendidos, pedidos recentes e relatório de cardápio).
- Se nenhuma aba do workbook apresentar colunas identificáveis de item/quantidade, o sistema SHALL responder `200 OK` com uma lista vazia e não persistir nenhuma alteração.

#### Scenario: Importar planilha de histórico de itens vendidos
- **WHEN** o usuário envia uma planilha `.xlsx` de histórico de itens vendidos
- **THEN** o sistema extrai cada linha como (nome do item, quantidade) e processa a importação

#### Scenario: Importar planilha de pedidos recentes
- **WHEN** o usuário envia uma planilha `.xlsx` de pedidos recentes
- **THEN** o sistema interpreta a célula de itens (nomes separados por `;`) e conta 1 unidade por ocorrência de cada nome

#### Scenario: Importar planilha de relatório de cardápio
- **WHEN** o usuário envia uma planilha `.xlsx` de relatório de cardápio
- **THEN** o sistema lê as abas com colunas de item/quantidade, soma os totais por nome e processa a importação

#### Scenario: Layout desconhecido com colunas identificáveis
- **WHEN** o usuário envia uma planilha `.xlsx` cujo cabeçalho/layout difere de todos os formatos anteriormente conhecidos, mas contém uma coluna textual de nomes de itens e uma coluna numérica de quantidades
- **THEN** o sistema identifica as colunas por heurística, extrai os pares (nome, quantidade) e processa a importação normalmente

#### Scenario: Colunas renomeadas ou reordenadas
- **WHEN** um layout previamente conhecido é enviado com colunas renomeadas ou reordenadas, mantendo nomes de itens e quantidades
- **THEN** o sistema identifica as colunas de nome e quantidade e processa a importação

#### Scenario: Workbook multi-abas com abas de dashboard
- **WHEN** um workbook contém abas sem colunas de item/quantidade (ex.: aba de funil/dashboard) além de abas com essas colunas
- **THEN** o sistema ignora as abas sem colunas identificáveis e extrai das abas que possuem colunas de nome e quantidade

#### Scenario: Mesmo item em mais de uma aba
- **WHEN** o mesmo nome de item aparece com quantidades em mais de uma aba do workbook
- **THEN** o sistema soma as quantidades por nome entre as abas

#### Scenario: Planilha sem colunas de item/quantidade
- **WHEN** nenhuma aba do workbook apresenta colunas identificáveis de nome de item e quantidade
- **THEN** o sistema responde `200 OK` com lista vazia e não persiste nenhuma alteração de estoque

#### Scenario: Extensão ou conteúdo inválido
- **WHEN** o arquivo enviado não termina em `.xlsx` ou está vazio
- **THEN** o sistema responde com `400 Bad Request` e não persiste nenhuma alteração