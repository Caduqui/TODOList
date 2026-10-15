## O que foi feito

Implementa o desafio de persistência com Local Storage no front-end do TODOList, conforme a Parte B de `docs/spec.md`. Os dois requisitos obrigatórios foram atendidos:

- **Persistência de tarefas no local storage.** As tarefas e seus alarmes são gravados na chave `todolist.tarefas` a cada alteração e restaurados ao abrir a página.
- **Opção de setar o status de múltiplas tarefas.** Cada card tem um checkbox; uma barra de ação permite escolher o status de destino e mover todas as tarefas marcadas de uma vez.

### Persistência

- Toda alteração é gravada na hora, sem botão de salvar: criar, editar e remover tarefa, mudar status em massa, adicionar e excluir alarme, e disparo de alarme.
- O `tarefas.json` só é carregado na primeira visita ou quando o conteúdo gravado está corrompido. Um quadro esvaziado pelo usuário continua vazio.
- O estado `disparado` dos alarmes é persistido: um alarme que já avisou não avisa de novo depois de recarregar.
- Se o navegador não conseguir gravar (storage cheio ou bloqueado), o quadro continua funcionando em memória e mostra um aviso.

### Mudança de status em massa

- Seleção por checkbox em qualquer coluna, inclusive em colunas diferentes ao mesmo tempo.
- Barra com contador ("3 selecionadas"), seletor "Mover para" (`BACKLOG`, `TODO`, `DOING`, `DONE`, iniciando em `DONE`) e botão "Executar", desabilitado sem seleção.
- Tarefas que já estão no status de destino são ignoradas. Uma mensagem informa quantas foram movidas.
- Só o status muda; a seleção não é gravada e é limpa depois de executar.

### Organização do código

A lógica da feature ficou em três classes sem acesso à tela, cada uma com sua classe de teste. O `main.js` continua cuidando da tela e passou a usá-las.

| Classe | Responsabilidade |
| --- | --- |
| `ArmazenamentoTarefas` | Ler e gravar a lista no local storage |
| `SelecaoTarefas` | Guardar as tarefas marcadas e montar o texto do contador |
| `MudancaStatusEmMassa` | Trocar o status das marcadas e montar a mensagem de resumo |

### Documentação

- `AGENTS.md`: contexto do projeto para agentes de código.
- `docs/spec.md` e `docs/plano.md`: especificação e plano de implementação em 11 tarefas, uma por commit.
- `src/README.md`: descrição da feature e de como rodar os testes.
- `skills/feature-com-spec/SKILL.md`: o fluxo spec → plano → implementação como skill reutilizável.

## Como testar

### Testes unitários

A partir da raiz do repositório, com Node instalado (sem `npm install`):

```bash
node --test "src/test/javascript/**/*.test.js"
```

Resultado esperado: 30 testes, 30 passando. Os testes estão em `src/test/javascript/front_end/`, no formato given / when / then, e cobrem as três classes.

### No navegador

A ligação das classes com a tela não tem teste automatizado; a conferência é manual.

1. Sirva a pasta `src/main/javascript/front_end/` por HTTP (por exemplo, pelo botão de abrir no navegador do IntelliJ sobre o `index.html`) e abra a página.
2. Abra o DevTools em Application → Local Storage.
3. **Persistência:** crie, edite e remova uma tarefa, recarregando a página depois de cada passo. O quadro volta como foi deixado e a chave `todolist.tarefas` acompanha as mudanças.
4. **Primeira visita:** apague a chave `todolist.tarefas` e recarregue. As 5 tarefas do seed voltam.
5. **Status em massa:** marque algumas tarefas, escolha o destino em "Mover para" e clique em "Executar". Elas mudam de coluna, aparece a mensagem de resumo e a seleção é limpa.
6. **Alarmes:** adicione um alarme a uma tarefa, recarregue e abra os alarmes dela. O alarme continua na lista.

## Critérios de aceite atendidos

Todos os 24 critérios da Parte B da spec. Cada tarefa foi conferida na tela pelo autor, como registrado em `docs/plano.md`.

### Persistência

- [x] CA-B01 — Primeira visita carrega o seed
- [x] CA-B02 — Tarefa criada sobrevive à recarga
- [x] CA-B03 — Edição sobrevive à recarga
- [x] CA-B04 — Remoção sobrevive à recarga
- [x] CA-B05 — Quadro vazio permanece vazio
- [x] CA-B06 — O seed não sobrescreve os dados do usuário
- [x] CA-B07 — Alarmes sobrevivem à recarga
- [x] CA-B08 — Exclusão de alarme sobrevive à recarga
- [x] CA-B09 — Alarme disparado não repete
- [x] CA-B10 — Dados corrompidos não quebram a página

### Mudança de status em massa

- [x] CA-B11 — Cards têm checkbox
- [x] CA-B12 — Contador acompanha a seleção
- [x] CA-B13 — Executar desabilitado sem seleção
- [x] CA-B14 — "Mover para" lista os quatro status
- [x] CA-B15 — Mover várias tarefas de uma vez
- [x] CA-B16 — Puxar tarefas do BACKLOG
- [x] CA-B17 — Seleção entre colunas
- [x] CA-B18 — Tarefas já no destino são ignoradas
- [x] CA-B19 — Nenhuma tarefa alterada
- [x] CA-B20 — Mudança em massa sobrevive à recarga
- [x] CA-B21 — Seleção sobrevive a um redesenho
- [x] CA-B22 — Tarefa removida sai da seleção
- [x] CA-B23 — Só o status muda
- [x] CA-B24 — `DONE` e `BACKLOG` em massa silenciam alarmes

## Uso de Tokens

┌──────────────────────────────────────────────────────────┬─────────┬─────────┬──────────────┬────────────┐
│                        Atividade                         │ Pedidos │  Saída  │ Entrada nova │ Cache lido │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Etapa 2: AGENTS.md                                       │ 1       │ 9.284   │ 40.569       │ 365.856    │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Etapa 3: spec (perguntas, escrita e inclusão do BACKLOG) │ 2       │ 37.867  │ 39.695       │ 566.607    │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Coluna BACKLOG (implementação, feature separada)         │ 1       │ 14.549  │ 20.017       │ 706.197    │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Etapa 4: plano (proposta, plano.md, merge, ajuste)       │ 4       │ 29.281  │ 31.838       │ 1.826.756  │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Etapa 5: implementação incremental (tarefas 1 a 10)      │ 12      │ 79.939  │ 95.287       │ 12.536.733 │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Etapa 6: validação e documentação (tarefa 11)            │ 1       │ 19.265  │ 27.930       │ 1.441.038  │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Skill feature-com-spec                                   │ 1       │ 8.535   │ 18.812       │ 931.451    │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Descrição do PR                                          │ 1       │ 4.419   │ 2.570        │ 981.435    │
├──────────────────────────────────────────────────────────┼─────────┼─────────┼──────────────┼────────────┤
│ Total                                                    │ 23      │ 203.139 │ 276.718      │ 19.356.073 │
└──────────────────────────────────────────────────────────┴─────────┴─────────┴──────────────┴────────────┘

## Momento de erro

O agente sempre pedia validações minha para cada tarefa executada, na tarefa 4 da etapa 5 ele pediu confirmação da minha visualização na página, fui lá, olhei a página notei que estava tudo ok na implementação e atualizei ele "olhei a página e está tudo ok" esperando que ele alterasse somente o plano.md, mas acabou que ele começou logo a implementar a próxima tarefa. Como resultado disso tive que explicar a ele que ele se antecipou e iniciou uma tarefa sem ao menos eu ter perdido, o que acarretou em um gasto maior de tokens pois ainda não tinha feito o commit e tive que retroceder, fazer o commit e implementar novamente a tarefa 5. 

## Observações

- **Escopo:** só o front-end foi alterado. Nenhum arquivo Java nem o `tarefas.json` foram tocados.
- **Sem dependências novas:** os testes usam o executor que já vem no Node.
- **Fora do escopo**, conforme a spec: sincronização entre abas, desfazer, "selecionar todas", arrastar e soltar, e rearmar alarmes já disparados ao editar a data da tarefa.

