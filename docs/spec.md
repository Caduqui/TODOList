# Spec: coluna BACKLOG, persistência no local storage e mudança de status em massa

Branch: `feature/localstorage`

O trabalho está dividido em duas partes, entregues nesta ordem:

| Parte | O que entrega | Onde mexe | Situação |
| --- | --- | --- | --- |
| **A** | Coluna e status `BACKLOG` | Front-end e CLI Java | Implementada |
| **B** | Persistência no local storage e mudança de status em massa | Somente front-end | Implementada |

A Parte B parte do princípio de que a Parte A já está pronta. Fazer o BACKLOG antes da persistência evita ter de migrar dados já gravados no navegador.

---

# Parte A: coluna BACKLOG

## A1. Contexto e objetivo

O quadro tem hoje três colunas: `TODO`, `DOING` e `DONE`. Toda tarefa nova cai direto em `TODO`, então a coluna mistura o que já foi escolhido para ser feito com o que só foi anotado para um dia. Um quadro kanban precisa de um lugar anterior ao `TODO` para guardar o que ainda não foi priorizado: o backlog.

Objetivo: acrescentar o status `BACKLOG`, anterior a `TODO`, como ponto de entrada das tarefas, no kanban e no CLI.

## A2. Regras de negócio

**RA-01 — Novo status.** Passa a existir o status `BACKLOG`. A ordem dos status é `BACKLOG`, `TODO`, `DOING`, `DONE`, e ela vale para as colunas do kanban (da esquerda para a direita), para todo seletor de status do front-end e para os textos do CLI.

**RA-02 — Coluna no kanban.** O quadro ganha a coluna `BACKLOG` à esquerda de `TODO`. Ela se comporta como as demais: mesmos cards, mesmos botões (editar, remover, alarme) e ordenação por prioridade decrescente. As quatro colunas ficam lado a lado, na mesma linha.

**RA-03 — Status inicial.** Toda tarefa nova nasce em `BACKLOG`:

- no CLI, a tarefa criada pela opção 1 começa em `BACKLOG` (o CLI não pergunta o status na criação);
- no front-end, o formulário de nova tarefa abre com `BACKLOG` selecionado, e o usuário ainda pode escolher outro status antes de salvar;
- uma tarefa do seed sem o campo `status` é tratada como `BACKLOG` (hoje todas as 5 têm status).

**RA-04 — Alarmes não disparam em BACKLOG.** Enquanto a tarefa está em `BACKLOG`, seus alarmes não avisam, como já acontece em `DONE`. O alarme suprimido não é marcado como disparado. Continua sendo possível adicionar, listar e excluir alarmes de uma tarefa em `BACKLOG`.

**RA-05 — Saída do BACKLOG.** Quando a tarefa vai de `BACKLOG` para `TODO` ou `DOING`, os alarmes ainda não disparados voltam a valer:

- se o horário do alarme já passou e o vencimento ainda não, o aviso aparece;
  - no CLI, na hora da mudança de status;
  - no front-end, na próxima verificação periódica (em até 60 s);
- se o horário do alarme ainda não chegou, ele dispara normalmente no horário;
- se o vencimento já passou, o alarme não dispara (regra atual).

**RA-06 — CLI.** As opções do menu continuam as mesmas. "Atualizar status" e "Filtrar por status" passam a aceitar `BACKLOG`, e os textos que listam os status mostram os quatro. Um status inválido continua resultando em mensagem de erro, como hoje.

**RA-07 — Seed.** O `tarefas.json` não é alterado. Na primeira abertura, a coluna `BACKLOG` aparece vazia.

### Casos de borda

| Caso | Comportamento esperado |
| --- | --- |
| Tarefa criada em `BACKLOG` recebe um alarme já dentro da janela de antecedência | Sem aviso enquanto estiver em `BACKLOG`; o aviso aparece ao ir para `TODO`/`DOING` (RA-05). |
| Tarefa em `TODO` com alarme pendente volta para `BACKLOG` | O alarme fica em silêncio até ela sair de novo. |
| Tarefa vai de `BACKLOG` direto para `DONE` | Nenhum aviso. |
| Vencimento passa com a tarefa em `BACKLOG` | O alarme nunca dispara, mesmo que ela saia depois. |
| Alarme já disparado; a tarefa vai para `BACKLOG` e volta | O alarme não repete. |
| Status digitado em minúsculas no CLI (`backlog`) | Aceito, como os demais. |
| Tarefa existente editada no front-end | O formulário abre com o status atual dela, não com `BACKLOG`. |

## A3. Critérios de aceite

### Front-end

**CA-A01 — Coluna existe e está na posição certa**
- Dado que abro o quadro
- Quando olho as colunas
- Então vejo `BACKLOG`, `TODO`, `DOING` e `DONE`, nessa ordem, da esquerda para a direita e na mesma linha

**CA-A02 — Seed continua igual**
- Dado a primeira abertura do quadro
- Quando a página carrega
- Então a coluna `BACKLOG` está vazia
- E as 5 tarefas do seed estão nas mesmas colunas de antes

**CA-A03 — Tarefa nova nasce em BACKLOG**
- Dado que clico em "Criar nova tarefa"
- Quando preencho o formulário sem mexer no status e salvo
- Então o seletor de status estava em `BACKLOG`
- E a tarefa aparece na coluna `BACKLOG`

**CA-A04 — Usuário ainda pode escolher outro status**
- Dado o formulário de nova tarefa aberto
- Quando escolho `TODO` e salvo
- Então a tarefa aparece na coluna `TODO`

**CA-A05 — Seletor lista os quatro status**
- Dado o formulário de tarefa aberto
- Quando abro o seletor de status
- Então vejo `BACKLOG`, `TODO`, `DOING` e `DONE`, nessa ordem

**CA-A06 — Mover para o BACKLOG pela edição**
- Dado uma tarefa em `TODO`
- Quando edito e troco o status para `BACKLOG`
- Então ela aparece na coluna `BACKLOG`

**CA-A07 — Ordenação no BACKLOG**
- Dado duas tarefas em `BACKLOG`, com prioridades 2 e 5
- Quando olho a coluna
- Então a de prioridade 5 aparece acima

**CA-A08 — Alarme em silêncio no BACKLOG**
- Dado uma tarefa em `BACKLOG`, dentro da janela de antecedência de um alarme ainda não disparado
- Quando a verificação de alarmes roda
- Então nenhum aviso é exibido

**CA-A09 — Alarme volta a valer ao sair do BACKLOG**
- Dado a tarefa do CA-A08
- Quando edito e troco o status para `TODO`
- Então o aviso do alarme aparece em até 1 minuto

### CLI

**CA-A10 — Tarefa nova nasce em BACKLOG**
- Dado o CLI em execução
- Quando crio uma tarefa pela opção 1 e listo pela opção 2
- Então a tarefa aparece com `Status: BACKLOG`

**CA-A11 — Atualizar status aceita BACKLOG**
- Dado uma tarefa em `TODO`
- Quando uso a opção 7 e informo `BACKLOG`
- Então vejo "Status atualizado!"
- E o texto da pergunta lista `BACKLOG, TODO, DOING, DONE`

**CA-A12 — Filtro por status aceita BACKLOG**
- Dado uma tarefa em `BACKLOG` e outra em `TODO`
- Quando uso a opção 6 e informo `BACKLOG`
- Então só a tarefa em `BACKLOG` é listada

**CA-A13 — Alarme em silêncio no BACKLOG**
- Dado uma tarefa em `BACKLOG` que vence em 1 hora
- Quando adiciono um alarme de prioridade 1 (2 horas antes)
- Então vejo "Alarme adicionado" e nenhum "ALARME!!!"

**CA-A14 — Alarme avisa ao sair do BACKLOG**
- Dado a tarefa do CA-A13
- Quando atualizo o status para `TODO`
- Então o "ALARME!!!" dessa tarefa é exibido na hora

**CA-A15 — BACKLOG direto para DONE não avisa**
- Dado uma tarefa como a do CA-A13
- Quando atualizo o status para `DONE`
- Então nenhum "ALARME!!!" é exibido

**CA-A16 — Build**
- Dado as alterações da Parte A
- Quando rodo `./gradlew build`
- Então o build termina com sucesso

## A4. Impacto no que já existe

| Funcionalidade atual | Impacto |
| --- | --- |
| **Valores de status** | Muda. De três para quatro, iguais no front-end e no CLI. |
| **Status inicial** | Muda. De `TODO` para `BACKLOG`. No CLI não há como escolher o status na criação, então toda tarefa precisa da opção 7 para chegar a `TODO`. |
| **Alarmes: disparo** | Muda. Além de `DONE`, `BACKLOG` também silencia. Como a tarefa nasce em `BACKLOG`, um alarme só passa a avisar depois que ela é movida para `TODO` ou `DOING`. |
| **Alarmes: CLI** | Muda. "Atualizar status" passa a reconferir os alarmes quando a tarefa sai de `BACKLOG`. |
| **Alarmes: tabela de antecedência, adicionar e excluir** | Sem mudança. |
| **Layout do kanban** | Muda. Quatro colunas em vez de três; o quadro fica mais largo e, em janelas estreitas, as colunas encolhem. |
| **Formulário de tarefa** | O seletor de status ganha `BACKLOG` e passa a abrir nele para tarefas novas. |
| **Edição de tarefa** | Sem mudança de comportamento; ganha a opção `BACKLOG`. |
| **Filtro por status (CLI)** | Passa a aceitar `BACKLOG`. |
| **Filtros por categoria e prioridade (CLI)** | Sem impacto. |
| **Ordenação por prioridade** | Sem mudança de regra; vale também na coluna nova. |
| **Seed (`tarefas.json`)** | Sem alteração. |

Arquivos previstos: `Tarefa.java`, `ConfigTarefas.java`, `Main.java`, `index.html`, `main.js` e, se o layout exigir, `style.css`.

Documentação a atualizar na entrega: `AGENTS.md` (regras de domínio e funcionalidades) e `src/README.md` (que cita "TODO, DOING ou DONE").

## A5. Fora do escopo

A Parte A **não** vai:

- alterar o `tarefas.json`;
- renomear ou remover os status existentes;
- criar novas opções no menu do CLI nem perguntar o status na criação pelo CLI;
- reconferir alarmes no CLI quando a tarefa volta de `DONE` (continua como hoje);
- oferecer arrastar e soltar entre colunas ou limite de tarefas por coluna;
- tornar o layout responsivo para telas pequenas;
- corrigir os pontos de atenção do `AGENTS.md`.

---

# Parte B: persistência no local storage e mudança de status em massa

Escopo: somente o front-end (`src/main/javascript/front_end/`).

## B1. Contexto e objetivo

Hoje o quadro kanban guarda as tarefas apenas em memória. A cada abertura da página ele relê o `tarefas.json`, então tudo o que o usuário criou, editou, removeu ou configurou de alarme se perde ao recarregar. Além disso, a única forma de mudar o status de uma tarefa é abrir a edição dela, uma por vez.

Isso gera dois problemas para quem usa o quadro no dia a dia:

1. **O trabalho não sobrevive à recarga.** O quadro não serve como controle real de atividades.
2. **Atualizar várias tarefas é repetitivo.** Quem conclui dez tarefas na semana precisa abrir e salvar dez formulários.

A feature resolve os dois:

- **Persistência:** as tarefas (com seus alarmes) passam a ser gravadas no local storage do navegador e restauradas ao abrir a página.
- **Status em massa:** o usuário marca várias tarefas, escolhe o status de destino e aplica a mudança de uma vez.

Caso de referência: Sandubinha cria várias tarefas numa semana cheia, conclui todas sem atualizar o quadro e, no fim, marca todas e move para `DONE` em uma única ação. Ao voltar no dia seguinte, o quadro está como ele deixou.

## B2. Regras de negócio

### B2.1 Persistência

**RB-01 — Onde e o que é gravado.** A lista completa de tarefas é gravada em uma única chave do local storage, `todolist.tarefas`, como um array JSON. Cada tarefa mantém as chaves que já existem hoje:

```json
{
  "id": 1,
  "name": "Comprar café",
  "description": "Estoque para a semana",
  "date": "2026-08-21T09:00",
  "priority": 1,
  "category": "Pessoal",
  "status": "BACKLOG",
  "alarmes": [{ "id": 1760000000000, "prioridade": 3, "disparado": false }]
}
```

**RB-02 — Quando grava.** Toda alteração da lista é gravada imediatamente, sem botão de salvar:

- criar tarefa;
- editar tarefa (qualquer campo, inclusive status);
- remover tarefa;
- mudar status em massa;
- adicionar alarme;
- excluir alarme;
- disparo de alarme (o alarme passa a `disparado: true`).

**RB-03 — Carga ao abrir a página.**

| Situação da chave `todolist.tarefas` | Comportamento |
| --- | --- |
| Não existe (primeira visita) | Carrega o `tarefas.json`, normaliza (status padrão `BACKLOG`, `alarmes` padrão `[]`) e grava no local storage. |
| Existe com um array válido, mesmo vazio | Usa o que está gravado. O `tarefas.json` não é lido. |
| Existe, mas o conteúdo não é um array JSON válido | Trata como primeira visita: registra o erro no console, carrega o `tarefas.json` e sobrescreve a chave. |

**RB-04 — Quadro vazio é um estado válido.** Se o usuário remover todas as tarefas, o quadro continua vazio nas próximas aberturas. O seed não volta sozinho.

**RB-05 — Falha ao carregar o seed.** Se o `tarefas.json` não puder ser lido na primeira visita, o quadro abre vazio e funcional, com o erro no console (como hoje). O que o usuário criar a partir daí é gravado normalmente.

**RB-06 — Falha ao gravar.** Se o local storage estiver indisponível ou cheio, a aplicação não quebra: continua funcionando em memória e mostra um aviso de que as alterações não estão sendo salvas.

**RB-07 — Identificadores estáveis.** O `id` de tarefas e alarmes não muda entre recargas.

**RB-08 — Estado dos alarmes.** O campo `disparado` é persistido. Um alarme que já avisou não avisa de novo depois de recarregar a página.

### B2.2 Mudança de status em massa

**RB-09 — Seleção.** Cada card tem um checkbox. O usuário pode marcar tarefas de qualquer coluna, inclusive de colunas diferentes ao mesmo tempo.

**RB-10 — Barra de ação.** Uma barra única no quadro mostra:

- o contador de tarefas marcadas (por exemplo, `3 selecionadas`);
- um seletor "Mover para" com `BACKLOG`, `TODO`, `DOING` e `DONE`, com `DONE` como valor inicial;
- o botão "Executar".

**RB-11 — Executar sem seleção.** Com nenhuma tarefa marcada, o botão "Executar" fica desabilitado.

**RB-12 — Aplicação.** Ao clicar em "Executar":

1. toda tarefa marcada cujo status é diferente do destino recebe o novo status;
2. tarefas marcadas que já estão no status de destino são ignoradas;
3. a lista é gravada no local storage (RB-02);
4. o quadro é redesenhado, com cada tarefa na coluna do novo status e na posição da ordenação por prioridade;
5. a seleção é limpa;
6. aparece uma mensagem de resumo.

A mudança é aplicada na hora, sem confirmação.

**RB-13 — Mensagem de resumo.** A mensagem conta apenas as tarefas que de fato mudaram:

- `3 tarefas movidas para DONE` (ou `1 tarefa movida para DONE`);
- `Nenhuma tarefa alterada: as selecionadas já estavam em DONE`, quando nenhuma mudou.

A mensagem não bloqueia a tela (não usa `alert`) e some quando o usuário volta a marcar ou desmarcar alguma tarefa.

**RB-14 — Vida da seleção.** A seleção vale enquanto a página está aberta e não é gravada no local storage:

- sobrevive a redesenhos do quadro (criar ou editar outra tarefa não desmarca nada);
- uma tarefa removida sai da seleção e o contador é atualizado;
- recarregar a página limpa a seleção.

**RB-15 — Só o status muda.** A ação em massa não altera nenhum outro campo da tarefa nem seus alarmes. As regras de alarme por status da Parte A (RA-04 e RA-05) valem igualmente para mudanças feitas em massa.

### B2.3 Casos de borda

| Caso | Comportamento esperado |
| --- | --- |
| Todas as marcadas já estão no destino | Nada muda; mensagem "Nenhuma tarefa alterada"; seleção é limpa. |
| Marcadas em colunas diferentes, destino igual a uma delas | Só as de outras colunas mudam e entram na contagem. |
| Tarefa marcada é removida antes de executar | Sai da seleção; as demais continuam marcadas. |
| Tarefa marcada é editada antes de executar | Continua marcada, já com os dados novos. |
| Tarefa com alarme pendente vai para `DONE` ou `BACKLOG` | O alarme não dispara enquanto ela estiver nesse status. |
| Tarefa sai de `BACKLOG` ou `DONE` para `TODO`/`DOING` | Alarmes ainda não disparados voltam a valer; os já disparados não repetem. |
| Usuário apaga todas as tarefas e recarrega | Quadro vazio (RB-04). |
| Conteúdo da chave corrompido | Recupera pelo seed (RB-03). |
| Aviso de alarme aberto e a página é recarregada | O aviso some e não volta, pois o alarme já está como disparado. |
| Duas abas abertas ao mesmo tempo | Sem sincronização: vale a última gravação. Ver "Fora do escopo". |

## B3. Critérios de aceite

### Persistência

**CA-B01 — Primeira visita carrega o seed**
- Dado que a chave `todolist.tarefas` não existe no local storage
- Quando abro a página
- Então vejo as 5 tarefas do `tarefas.json` nas colunas corretas e a coluna `BACKLOG` vazia
- E a chave `todolist.tarefas` passa a existir com essas 5 tarefas

**CA-B02 — Tarefa criada sobrevive à recarga**
- Dado que o quadro está carregado
- Quando crio uma tarefa pelo formulário e recarrego a página
- Então a tarefa aparece na mesma coluna, com os mesmos dados

**CA-B03 — Edição sobrevive à recarga**
- Dado uma tarefa existente
- Quando edito seu nome e seu status e recarrego a página
- Então vejo o nome novo, na coluna do status novo

**CA-B04 — Remoção sobrevive à recarga**
- Dado uma tarefa existente
- Quando a removo e recarrego a página
- Então ela não aparece mais

**CA-B05 — Quadro vazio permanece vazio**
- Dado que removi todas as tarefas
- Quando recarrego a página
- Então o quadro continua vazio e o seed não é recarregado

**CA-B06 — O seed não sobrescreve os dados do usuário**
- Dado que já existem tarefas gravadas no local storage
- Quando abro a página
- Então vejo exatamente as tarefas gravadas, e não as do `tarefas.json`

**CA-B07 — Alarmes sobrevivem à recarga**
- Dado uma tarefa à qual adicionei um alarme de prioridade 3
- Quando recarrego a página e abro os alarmes da tarefa
- Então vejo o alarme "Prioridade 3 - 6h antes" na lista

**CA-B08 — Exclusão de alarme sobrevive à recarga**
- Dado uma tarefa com um alarme
- Quando excluo o alarme e recarrego a página
- Então a lista de alarmes da tarefa está vazia

**CA-B09 — Alarme disparado não repete**
- Dado uma tarefa em `TODO` ou `DOING`, dentro da janela de antecedência, cujo alarme já exibiu o aviso
- Quando recarrego a página
- Então o aviso desse alarme não é exibido de novo

**CA-B10 — Dados corrompidos não quebram a página**
- Dado que a chave `todolist.tarefas` contém um texto que não é JSON válido
- Quando abro a página
- Então o quadro carrega com as tarefas do seed
- E a chave volta a conter um array válido

### Mudança de status em massa

**CA-B11 — Cards têm checkbox**
- Dado o quadro com tarefas
- Quando olho qualquer card, em qualquer das quatro colunas
- Então vejo um checkbox desmarcado, além dos botões de editar, remover e alarme

**CA-B12 — Contador acompanha a seleção**
- Dado nenhuma tarefa marcada
- Quando marco 3 tarefas
- Então a barra mostra "3 selecionadas"

**CA-B13 — Executar desabilitado sem seleção**
- Dado nenhuma tarefa marcada
- Quando olho a barra de ação
- Então o botão "Executar" está desabilitado

**CA-B14 — "Mover para" lista os quatro status**
- Dado a barra de ação
- Quando abro o seletor "Mover para"
- Então vejo `BACKLOG`, `TODO`, `DOING` e `DONE`, com `DONE` selecionado ao abrir a página

**CA-B15 — Mover várias tarefas de uma vez (caso Sandubinha)**
- Dado 4 tarefas em `TODO` marcadas e "Mover para" em `DONE`
- Quando clico em "Executar"
- Então as 4 tarefas aparecem na coluna `DONE`, ordenadas por prioridade
- E vejo a mensagem "4 tarefas movidas para DONE"
- E nenhuma tarefa fica marcada

**CA-B16 — Puxar tarefas do BACKLOG**
- Dado 3 tarefas em `BACKLOG` marcadas e "Mover para" em `TODO`
- Quando clico em "Executar"
- Então as 3 aparecem na coluna `TODO` e a mensagem é "3 tarefas movidas para TODO"

**CA-B17 — Seleção entre colunas**
- Dado 1 tarefa em `BACKLOG`, 1 em `TODO` e 1 em `DOING` marcadas, e "Mover para" em `DONE`
- Quando clico em "Executar"
- Então as 3 aparecem em `DONE` e a mensagem é "3 tarefas movidas para DONE"

**CA-B18 — Tarefas já no destino são ignoradas**
- Dado 1 tarefa em `TODO` e 1 em `DONE` marcadas, e "Mover para" em `DONE`
- Quando clico em "Executar"
- Então a mensagem é "1 tarefa movida para DONE"
- E a tarefa que já estava em `DONE` continua igual

**CA-B19 — Nenhuma tarefa alterada**
- Dado 2 tarefas em `DONE` marcadas e "Mover para" em `DONE`
- Quando clico em "Executar"
- Então a mensagem é "Nenhuma tarefa alterada: as selecionadas já estavam em DONE"
- E a seleção é limpa

**CA-B20 — Mudança em massa sobrevive à recarga**
- Dado que movi 3 tarefas para `DOING` pela ação em massa
- Quando recarrego a página
- Então as 3 continuam em `DOING`

**CA-B21 — Seleção sobrevive a um redesenho**
- Dado 2 tarefas marcadas
- Quando crio uma nova tarefa pelo formulário
- Então as 2 continuam marcadas e o contador mostra "2 selecionadas"

**CA-B22 — Tarefa removida sai da seleção**
- Dado 2 tarefas marcadas
- Quando removo uma delas
- Então o contador mostra "1 selecionada"

**CA-B23 — Só o status muda**
- Dado uma tarefa com um alarme, marcada
- Quando a movo para `DOING` pela ação em massa
- Então nome, descrição, data, prioridade, categoria e alarmes continuam iguais

**CA-B24 — `DONE` e `BACKLOG` em massa silenciam alarmes**
- Dado uma tarefa em `TODO` com um alarme ainda não disparado
- Quando a movo para `DONE` ou para `BACKLOG` pela ação em massa e o horário do alarme chega
- Então nenhum aviso é exibido para essa tarefa

A verificação dos critérios de front-end, das duas partes, é manual, no navegador, com a página servida por HTTP (ver `AGENTS.md`). Na Parte B, a lógica de persistência e de status em massa também é coberta por testes unitários, descritos em `docs/plano.md`.

## B4. Impacto no que já existe

| Funcionalidade atual | Impacto |
| --- | --- |
| **Carga inicial (`fetch` do `tarefas.json`)** | Muda. Passa a acontecer só na primeira visita ou na recuperação de dados corrompidos. O arquivo `tarefas.json` em si não é alterado. |
| **Criar, editar e remover tarefa** | O comportamento na tela é o mesmo; cada ação passa a gravar no local storage. |
| **Status individual (via edição)** | Continua funcionando igual e convive com a ação em massa. |
| **Ordenação por prioridade** | Sem mudança de regra. Tarefas movidas em massa entram na ordenação da coluna de destino. |
| **Alarmes: adicionar e excluir** | Passam a ser gravados. |
| **Alarmes: disparo** | Muda um efeito colateral. Hoje, recarregar a página zera o `disparado` e o aviso reaparece; com a persistência, um alarme avisa uma única vez. Consequência: se o usuário adiar o vencimento de uma tarefa cujo alarme já disparou, esse alarme não dispara de novo. |
| **Alarmes: regras de disparo** | Sem mudança em relação à Parte A: tabela de antecedência, silêncio em `BACKLOG` e `DONE`, não disparar após o vencimento e ciclo de verificação (ao carregar, ao adicionar alarme e a cada 60 s). |
| **Layout do card** | Ganha um checkbox. Os botões de editar, remover e alarme permanecem. |
| **Layout do quadro** | Ganha a barra de ação em massa. |
| **Organização do front-end** | Muda. A lógica da feature fica em classes JavaScript, em arquivos novos ao lado do `main.js`, que continua cuidando da tela. Surge a pasta de testes `src/test/javascript/front_end/`. |
| **Filtros (categoria, prioridade, status)** | Sem impacto: existem só no CLI Java, que a Parte B não toca. |
| **Back-end Java (CLI)** | Sem impacto. Nenhum arquivo Java é alterado na Parte B. |

Efeitos para quem desenvolve:

- Alterar o `tarefas.json` deixa de refletir no navegador depois da primeira visita. Para ver o seed de novo, é preciso apagar a chave `todolist.tarefas` nas ferramentas do navegador.
- O texto das tarefas continua sendo inserido no HTML sem escape, como hoje. Com a persistência, um conteúdo malformado digitado em uma tarefa passa a ser recarregado a cada abertura, em vez de sumir na recarga.

## B5. Fora do escopo

A Parte B **não** vai:

- alterar o back-end Java nem integrá-lo ao front-end;
- oferecer outras ações em massa (remover, editar campos, criar alarmes);
- oferecer "selecionar todas", por coluna ou geral;
- oferecer arrastar e soltar entre colunas;
- adicionar filtros ou busca ao front-end;
- sincronizar abas abertas ao mesmo tempo;
- oferecer desfazer, exportar, importar ou um botão de "restaurar dados iniciais";
- versionar ou migrar o formato dos dados gravados;
- persistir a seleção, o formulário aberto ou outro estado de interface;
- rearmar alarmes já disparados quando a data da tarefa é editada;
- corrigir os pontos de atenção do `AGENTS.md` (formatos de data diferentes, CLI que não encerra, caminho no README) nem escapar o HTML dos textos das tarefas;
- adicionar dependências ou ferramentas de build (os testes usam o executor que já vem no Node);
- escrever testes para o BACKLOG ou para funcionalidades anteriores à Parte B.

---

# Decisões

Todas confirmadas pelo autor. Não há premissas em aberto.

| Parte | Decisão |
| --- | --- |
| A | `BACKLOG` entra no front-end e no CLI Java. |
| A | O status inicial de toda tarefa passa a ser `BACKLOG`. |
| A | Alarmes não disparam em `BACKLOG`. |
| A | O CLI reconfere os alarmes quando a tarefa sai de `BACKLOG`. |
| A | No front-end, o aviso de um alarme liberado ao sair de `BACKLOG` segue o ciclo de verificação que já existe (em até 60 s), em vez de aparecer na hora como no CLI. |
| A | O `tarefas.json` não é alterado. |
| A | Tarefa do seed sem `status` passa a cair em `BACKLOG`, acompanhando o novo status inicial. |
| A | O título da coluna e o valor do status são ambos `BACKLOG`, em maiúsculas, como os demais. |
| A | A spec da Parte A é revisada pelo autor antes de qualquer código. |
| A e B | `AGENTS.md` e `src/README.md` são atualizados como parte de cada entrega. |
| B | Seed carregado apenas na primeira visita. |
| B | Nome da chave: `todolist.tarefas`. |
| B | Dados corrompidos são substituídos pelo seed (RB-03), em vez de preservados. |
| B | Alarmes persistidos junto com o estado `disparado`. |
| B | Seleção por checkbox em cada card, com barra de ação única. |
| B | Valor inicial de "Mover para": `DONE`. |
| B | "Executar" fica desabilitado sem seleção, em vez de mostrar uma mensagem de erro. |
| B | "Executar" aplica na hora e mostra um resumo. |
| B | Testes unitários em JavaScript, rodados com `node --test`, sem dependência nova. |
| B | A lógica da feature fica em classes JavaScript em arquivos novos, cada uma com sua classe de teste. |
| B | Só a Parte B ganha testes. |
| B | Teste escrito antes do código, no formato Given / When / Then. |
