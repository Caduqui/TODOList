# Plano de implementação: persistência no local storage e status em massa

Implementa a **Parte B** de `docs/spec.md`. Os códigos `RB-xx` e `CA-Bxx` citados aqui são as regras e os critérios de aceite da spec. Leia também o `AGENTS.md` antes de começar.

Todas as tarefas mexem só no front-end. Nenhum arquivo Java, nem o `tarefas.json`, é alterado.

## Decisões de implementação

| Tema | Decisão |
| --- | --- |
| Testes | Unitários, em JavaScript, rodados com o executor que já vem no Node (`node --test`). Sem npm, sem `package.json`, sem dependência nova. |
| Escopo dos testes | Somente a Parte B (persistência e status em massa). BACKLOG e funcionalidades anteriores não ganham testes. |
| Organização do código | A lógica da feature fica em classes JavaScript, uma por arquivo, sem acesso à tela. O `main.js` continua cuidando da tela e passa a usar essas classes. |
| Organização dos testes | Uma classe de teste por classe da feature, na pasta `src/test/javascript/front_end/`, que espelha a pasta do código. |
| Ordem | Teste antes do código: escrever o teste, vê-lo falhar, implementar, vê-lo passar. |
| Formato | Given / When / Then no nome de cada teste e em comentários no corpo. |

Essas decisões alteram duas convenções do `AGENTS.md` ("um único `main.js`" e "não há testes de front-end"). A alteração foi aprovada pelo autor e o `AGENTS.md` é atualizado na tarefa 11.

## Estrutura ao final

```
src/
├── main/javascript/front_end/
│   ├── index.html                     # alterado
│   ├── main.js                        # alterado
│   ├── style.css                      # alterado
│   ├── ArmazenamentoTarefas.js        # novo
│   ├── SelecaoTarefas.js              # novo
│   └── MudancaStatusEmMassa.js        # novo
└── test/javascript/front_end/         # novo
    ├── ArmazenamentoTarefas.test.js
    ├── SelecaoTarefas.test.js
    └── MudancaStatusEmMassa.test.js
```

### Classes da feature

| Classe | Responsabilidade | Métodos |
| --- | --- | --- |
| `ArmazenamentoTarefas` | Ler e gravar a lista na chave `todolist.tarefas`. Recebe o storage no construtor, para o teste poder passar um falso. | `salvar(tarefas)` devolve `true` ou `false`; `carregar()` devolve a lista ou `null` |
| `SelecaoTarefas` | Guardar os `id` das tarefas marcadas. | `alternar(id, marcada)`, `estaSelecionada(id)`, `remover(id)`, `limpar()`, `quantidade()`, `ids()`, `textoContador()` |
| `MudancaStatusEmMassa` | Trocar o status das tarefas marcadas e montar a mensagem de resumo. | `aplicar(tarefas, ids, statusDestino)` devolve quantas mudaram; `resumo(quantidade, statusDestino)` devolve o texto |

Convenções desses arquivos:

- nome do arquivo igual ao da classe, em PascalCase e em português, como nas classes Java;
- métodos em camelCase e em português;
- sem `import`/`export`: cada arquivo é carregado por `<script>` antes do `main.js` e termina com o trecho abaixo, que só tem efeito no Node e permite o `require` nos testes.

```js
if (typeof module !== "undefined") {
    module.exports = { ArmazenamentoTarefas };
}
```

## Como rodar os testes

A partir da raiz do repositório, no PowerShell ou no bash:

```bash
node --test "src/test/javascript/**/*.test.js"
```

Passar a pasta em vez do padrão (`node --test src/test/javascript/`) não funciona nesta versão do Node.

### Formato de um teste

```js
const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { ArmazenamentoTarefas } = require("../../../main/javascript/front_end/ArmazenamentoTarefas.js");

describe("ArmazenamentoTarefas", () => {
    it("given chave ausente, when carregar, then devolve null", () => {
        // given
        const armazenamento = new ArmazenamentoTarefas(storageVazio());

        // when
        const tarefas = armazenamento.carregar();

        // then
        assert.equal(tarefas, null);
    });
});
```

### O que os testes cobrem e o que não cobrem

Os testes unitários cobrem as três classes. A ligação delas com a tela (desenhar checkbox, clicar em botão, mostrar aviso) fica no `main.js` e não é testável sem um navegador simulado, que exigiria dependência nova. Por isso as tarefas de ligação (3, 4, 5, 6, 9 e 10) não criam testes: são validadas na tela, com a suíte inteira continuando verde.

A conferência manual é feita com a página servida por HTTP e o DevTools aberto em Application → Local Storage.

## Tarefas

### 1. `ArmazenamentoTarefas.salvar`

- **O que:** criar a classe com o construtor e o método `salvar`, que grava a lista como JSON na chave `todolist.tarefas` e não deixa o erro de gravação escapar (RB-01, RB-06).
- **Arquivos:** criar `src/test/javascript/front_end/ArmazenamentoTarefas.test.js` e `src/main/javascript/front_end/ArmazenamentoTarefas.js`.
- **Testes (escritos antes):**
  - given uma lista com tarefas e alarmes, when salvar, then a chave `todolist.tarefas` recebe o JSON da lista e o retorno é `true`;
  - given uma lista vazia, when salvar, then a chave recebe `[]`;
  - given um storage que lança erro ao gravar, when salvar, then o retorno é `false` e nenhum erro é propagado.
- **Validação:** rodar a suíte antes da classe existir e ver os 3 testes falharem; implementar; rodar de novo e ver os 3 passarem.
- **Situação:** concluída.

### 2. `ArmazenamentoTarefas.carregar`

- **O que:** método `carregar`, que devolve a lista gravada ou `null` quando não há nada aproveitável (RB-03, RB-04, RB-07, RB-08).
- **Arquivos:** `ArmazenamentoTarefas.test.js`, `ArmazenamentoTarefas.js`.
- **Testes (escritos antes):**
  - given chave ausente, when carregar, then devolve `null`;
  - given um array válido gravado, when carregar, then devolve as mesmas tarefas, com `id`, alarmes e `disparado` preservados;
  - given um array vazio gravado, when carregar, then devolve `[]`, e não `null`;
  - given um texto que não é JSON, when carregar, then devolve `null`;
  - given um JSON válido que não é array, when carregar, then devolve `null`;
  - given um storage que lança erro ao ler, when carregar, then devolve `null`;
  - given uma lista salva com `salvar`, when carregar, then devolve uma lista igual.
- **Validação:** os 7 testes novos falham antes e passam depois; os 3 da tarefa 1 continuam passando.
- **Situação:** concluída.

### 3. Gravar ao criar, editar e remover tarefa

- **O que:** carregar `ArmazenamentoTarefas.js` na página, criar a instância no `main.js` e chamar `salvar` depois de criar, editar e remover tarefa (RB-02).
- **Arquivos:** `index.html` (novo `<script>` antes do `main.js`), `main.js`.
- **Validação:**
  - suíte continua verde;
  - na tela: criar uma tarefa e ver a chave `todolist.tarefas` aparecer com ela; editar e ver o valor mudar; remover e ver a tarefa sair da chave;
  - recarregar ainda volta ao seed, o que é esperado até a tarefa 4.
- **Situação:** concluída; conferida na tela pelo autor.

### 4. Carregar do local storage ao abrir

- **O que:** trocar a carga inicial do `main.js`. Se `carregar` devolver uma lista, usá-la sem ler o seed. Se devolver `null`, buscar o `tarefas.json`, normalizar e gravar. Se o seed falhar, abrir o quadro vazio e funcional (RB-03, RB-04, RB-05).
- **Arquivos:** `main.js`.
- **Validação:**
  - suíte continua verde;
  - CA-B01 a CA-B06;
  - CA-B10: no console, `localStorage.setItem("todolist.tarefas", "abc")` e recarregar;
  - RB-05: apagar a chave, bloquear `tarefas.json` na aba Network e recarregar; o quadro abre vazio e uma tarefa criada é gravada.
- **Situação:** implementada; conferida na tela pelo autor.

### 5. Persistir os alarmes

- **O que:** chamar `salvar` ao adicionar alarme, ao excluir alarme e quando um alarme dispara (RB-02, RB-08).
- **Arquivos:** `main.js`.
- **Validação:**
  - suíte continua verde;
  - CA-B07 e CA-B08;
  - CA-B09: criar uma tarefa em `TODO` que vence em 1 hora, com alarme de prioridade 1. O aviso aparece, a chave mostra `"disparado": true` e, após recarregar, o aviso não volta.
- **Situação:** implementada; conferida na tela pelo autor.

### 6. Avisar quando não for possível gravar

- **O que:** quando `salvar` devolver `false`, mostrar um aviso fixo de que as alterações não estão sendo salvas. A aplicação segue funcionando em memória (RB-06).
- **Arquivos:** `index.html` (elemento do aviso), `main.js`, `style.css`.
- **Validação:**
  - suíte continua verde (o retorno `false` já é coberto pela tarefa 1);
  - no console, `Storage.prototype.setItem = () => { throw new Error("cheio") }` e criar uma tarefa: o card aparece, o aviso aparece e não há erro não tratado no console.
- **Situação:** implementada; conferida na tela pelo autor.

### 7. `SelecaoTarefas`

- **O que:** classe que guarda os `id` das tarefas marcadas e monta o texto do contador (RB-09, RB-10, RB-14).
- **Arquivos:** criar `src/test/javascript/front_end/SelecaoTarefas.test.js` e `src/main/javascript/front_end/SelecaoTarefas.js`.
- **Testes (escritos antes):**
  - given seleção vazia, when alternar um id como marcado, then a quantidade é 1 e o id está selecionado;
  - given um id selecionado, when alternar o mesmo id como desmarcado, then a quantidade é 0;
  - given um id selecionado, when alternar o mesmo id como marcado de novo, then a quantidade continua 1;
  - given dois ids selecionados, when remover um deles, then a quantidade é 1 e o outro continua selecionado;
  - given um id que não está selecionado, when remover, then nada muda;
  - given dois ids selecionados, when limpar, then a quantidade é 0;
  - given três ids selecionados, when ids, then devolve os três;
  - given três selecionados, when textoContador, then devolve "3 selecionadas";
  - given um selecionado, when textoContador, then devolve "1 selecionada";
  - given nenhum selecionado, when textoContador, then devolve "0 selecionadas".
- **Validação:** os 10 testes falham antes e passam depois; os anteriores continuam passando.
- **Situação:** concluída.

### 8. `MudancaStatusEmMassa`

- **O que:** classe com `aplicar`, que troca o status das tarefas marcadas que diferem do destino, e `resumo`, que monta a mensagem (RB-12, RB-13, RB-15).
- **Arquivos:** criar `src/test/javascript/front_end/MudancaStatusEmMassa.test.js` e `src/main/javascript/front_end/MudancaStatusEmMassa.js`.
- **Testes (escritos antes):**
  - given três tarefas em `TODO` selecionadas, when aplicar `DONE`, then as três ficam em `DONE` e o retorno é 3;
  - given tarefas selecionadas em `BACKLOG`, `TODO` e `DOING`, when aplicar `DONE`, then todas ficam em `DONE` e o retorno é 3;
  - given uma em `TODO` e uma em `DONE` selecionadas, when aplicar `DONE`, then o retorno é 1;
  - given todas as selecionadas já em `DONE`, when aplicar `DONE`, then o retorno é 0;
  - given uma tarefa não selecionada, when aplicar, then o status dela não muda;
  - given um id selecionado que não existe na lista, when aplicar, then ele é ignorado e as demais são tratadas;
  - given uma tarefa selecionada com alarmes, when aplicar, then só o status muda: nome, descrição, data, prioridade, categoria e alarmes continuam iguais;
  - given quantidade 3 e destino `DONE`, when resumo, then devolve "3 tarefas movidas para DONE";
  - given quantidade 1 e destino `DONE`, when resumo, then devolve "1 tarefa movida para DONE";
  - given quantidade 0 e destino `DONE`, when resumo, then devolve "Nenhuma tarefa alterada: as selecionadas já estavam em DONE".
- **Validação:** os 10 testes falham antes e passam depois; os anteriores continuam passando.

### 9. Seleção na tela e barra de ação

- **O que:** checkbox em cada card, ligado à `SelecaoTarefas` e redesenhado conforme a seleção; barra com contador, seletor "Mover para" (quatro status, `DONE` inicial) e botão "Executar" desabilitado sem seleção; remover uma tarefa a tira da seleção (RB-09, RB-10, RB-11, RB-14).
- **Arquivos:** `index.html` (barra e `<script>` da classe), `main.js`, `style.css`.
- **Validação:**
  - suíte continua verde;
  - CA-B11, CA-B12, CA-B13, CA-B14, CA-B21 e CA-B22;
  - recarregar limpa a seleção e a chave não guarda nada sobre ela;
  - "Executar" ainda não faz nada nesta etapa.

### 10. Botão "Executar"

- **O que:** no clique, usar `MudancaStatusEmMassa.aplicar`, gravar, limpar a seleção, redesenhar e mostrar o resumo. A mensagem some quando o usuário marca ou desmarca uma tarefa (RB-12, RB-13).
- **Arquivos:** `index.html` (`<script>` da classe), `main.js`; `style.css` se a mensagem precisar de estilo.
- **Validação:**
  - suíte continua verde;
  - CA-B15 a CA-B20, CA-B23 e CA-B24.

### 11. Regressão e documentação

- **O que:** atualizar a documentação para o estado final.
  - `AGENTS.md`: funcionalidades e regras da Parte B, nova estrutura de pastas, comando dos testes, convenção das classes e dos testes, remoção da seção de pendências.
  - `src/README.md`: descrição da feature e de como rodar os testes.
  - `docs/spec.md`: Parte B marcada como implementada.
- **Arquivos:** `AGENTS.md`, `src/README.md`, `docs/spec.md`.
- **Validação:**
  - `node --test "src/test/javascript/**/*.test.js"` passa com os 30 testes;
  - com o local storage limpo, repassar CA-B01 a CA-B24 em sequência;
  - repassar CA-A01 a CA-A09 e o fluxo de editar tarefa e abrir o modal de alarme;
  - `./gradlew build` passa;
  - `git status` não mostra nenhum arquivo Java nem o `tarefas.json` alterados pelas tarefas 1 a 11.

## Pendências

- **Commits:** o agente não faz commit do código das tarefas 1 a 11 sem autorização. As tarefas foram cortadas para que cada uma possa virar um commit.
