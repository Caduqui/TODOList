# AGENTS.md

Guia para agentes de código que trabalham neste repositório. Leia antes de alterar qualquer arquivo.

## Visão geral

TODOList é um gerenciador de tarefas com duas partes **independentes** (não há integração entre elas):

- **Back-end (Java, CLI):** menu interativo no terminal. Os dados ficam só em memória e se perdem ao encerrar.
- **Front-end (HTML/CSS/JS puro):** quadro kanban com colunas `BACKLOG`, `TODO`, `DOING` e `DONE`. Carrega dados iniciais de `tarefas.json` via `fetch` e mantém o estado em memória (perdido ao recarregar a página).

### Funcionalidades existentes

| Funcionalidade | CLI (Java) | Front-end |
| --- | --- | --- |
| Adicionar tarefa (nome, descrição, vencimento, prioridade 1–5, categoria) | sim | sim |
| Listar tarefas | sim | sim (kanban) |
| Remover tarefa por ID | sim | sim |
| Editar tarefa | não | sim |
| Atualizar status (`BACKLOG`, `TODO`, `DOING`, `DONE`) | sim | sim (via edição) |
| Filtrar por categoria, prioridade e status | sim | não |
| Ordenação por prioridade (5 = mais alta, vem primeiro) | sim | sim |
| Alarmes por tarefa (adicionar e disparar) | sim | sim |
| Listar e excluir alarmes | não | sim |

### Regras de domínio

- Prioridade da tarefa: inteiro de 1 a 5; a lista é sempre reordenada de forma decrescente.
- Status, nesta ordem: `BACKLOG`, `TODO`, `DOING`, `DONE`.
- Status inicial de toda tarefa: `BACKLOG`. No front-end o formulário abre em `BACKLOG` e deixa escolher outro; no CLI não há escolha na criação.
- Alarme: tem prioridade própria de 1 a 5, que define a antecedência em relação ao vencimento:

  | Prioridade | 1 | 2 | 3 | 4 | 5 |
  | --- | --- | --- | --- | --- | --- |
  | Horas antes | 2 | 4 | 6 | 12 | 24 |

  Essa tabela está duplicada em `Alarme.java` (`getHorasAntecedencia`), `main.js` (`getHorasAntecedencia`) e nas `<option>` de `#alarme_prioridade` em `index.html`. Qualquer mudança precisa ser feita nos três lugares.
- Um alarme dispara uma única vez, só antes do vencimento, e nunca para tarefas em `BACKLOG` ou `DONE`. Um alarme em silêncio não é marcado como disparado.
- Quando a tarefa sai de `BACKLOG`, os alarmes pendentes voltam a valer: o CLI reconfere na hora (`ConfigTarefas.atualizarStatus`); o front-end, na verificação periódica de `verificarAlarmes` (a cada 60 s).
- Os valores de status estão repetidos em vários lugares e precisam mudar juntos: enum `TarefaStatus` em `Tarefa.java`; textos do menu em `Main.java`; `id` das colunas e `<option>` de `#input_status` em `index.html`; e, em `main.js`, o status padrão do seed, o reset do formulário e a checagem de alarme.

### Mudanças especificadas e ainda não implementadas

`docs/spec.md` descreve o trabalho em andamento. As seções acima retratam o código como ele está hoje; o que segue ainda **não** está no código.

A Parte A (coluna `BACKLOG`) já está implementada e refletida acima. Falta:

- **Parte B, persistência e status em massa (só front-end):**
  - tarefas e alarmes gravados no local storage do navegador;
  - mudança de status de várias tarefas de uma vez.

O passo a passo está em `docs/plano.md`. Ele autoriza, para a Parte B, duas mudanças nas convenções abaixo: classes JavaScript em arquivos novos ao lado do `main.js` e testes unitários em `src/test/javascript/front_end/`, rodados com `node --test`.

Ao concluir a Parte B, atualize as seções "Funcionalidades existentes" e "Regras de domínio" e remova esta seção.

## Stack e estrutura

- **Java 25** (JDK configurado no IntelliJ), sem dependências de runtime.
- **Gradle 9.6.0** via wrapper, apenas com o plugin `java` (não há plugin `application`).
- **JUnit Jupiter** (BOM 6.0.0) já configurado em `build.gradle`, mas ainda sem testes.
- **Front-end:** HTML, CSS e JavaScript puros. Sem npm, sem bundler, sem framework, sem módulos ES.
- IDE: IntelliJ IDEA.

```
TODOList/
├── build.gradle, settings.gradle     # build Gradle (Groovy DSL)
├── gradlew, gradlew.bat, gradle/     # wrapper
├── docs/
│   ├── spec.md                       # especificação das features em andamento
│   └── plano.md                      # plano de implementação da Parte B, em tarefas
└── src/
    ├── README.md                     # README do projeto (fica em src/, não na raiz)
    └── main/
        ├── java/org/example/
        │   ├── Main.java             # menu do CLI e leitura de entrada (Scanner)
        │   ├── ConfigTarefas.java    # lista de tarefas, filtros, ordenação e agendamento de alarmes
        │   ├── Tarefa.java           # modelo + enum TarefaStatus
        │   └── Alarme.java           # modelo do alarme e cálculo de antecedência
        └── javascript/front_end/
            ├── index.html            # kanban, formulário de tarefa e modal de alarme
            ├── main.js               # todo o comportamento do front-end
            ├── style.css
            ├── tarefas.json          # dados iniciais (seed) de desenvolvimento
            └── assets/               # ícones PNG (editar, lixeira, alarme)
```

`src/test/` ainda não existe.

## Como executar e testar

Comandos a partir da raiz do repositório. No PowerShell use `.\gradlew.bat`; no bash, `./gradlew`.

```bash
# compilar (também roda os testes)
./gradlew build

# executar o CLI (não existe a task `gradlew run`)
java -cp build/classes/java/main org.example.Main

# rodar apenas os testes
./gradlew test
```

- Pelo IntelliJ: abrir `Main.java` e executar o método `main` (Shift+F10).
- **Testes:** hoje `./gradlew test` termina com `NO-SOURCE`. Testes novos vão em `src/test/java/org/example/`, com JUnit Jupiter.
- **Front-end:** precisa ser servido por HTTP, porque `main.js` usa `fetch("tarefas.json")` e isso falha ao abrir o `index.html` direto do disco (`file://`). Use o servidor embutido do IntelliJ (botão de abrir no navegador sobre o `index.html`) ou qualquer servidor estático apontando para `src/main/javascript/front_end/`. Não há testes automatizados de front-end; a verificação é manual no navegador.

### Pontos de atenção conhecidos

- O `src/README.md` indica `cd src/main/org.example`; o caminho real é `src/main/java/org/example`.
- No CLI, depois que um alarme é agendado, a opção `9- Sair` não encerra o processo: a thread do `ScheduledExecutorService` em `ConfigTarefas` segue viva.
- Formatos de data diferentes: o CLI lê `dd/MM/yyyy HH:mm`; o formulário do front usa `datetime-local` (`yyyy-MM-ddTHH:mm`); o seed em `tarefas.json` traz só a data (`yyyy-MM-dd`).
- IDs: no Java vêm de um contador estático em `Tarefa`; no front, de `Date.now()`.

Esses pontos são registro de contexto, não tarefas. Não os corrija fora do escopo pedido.

## Convenções

### Geral

- Código, mensagens ao usuário e commits em **português**.
- Indentação de 4 espaços em Java, JS, HTML e CSS.
- Commits curtos e descritivos, no estilo do histórico (`Implementação do alarme`, `Implementado front-end com javascript`). Branches de feature como `feature/<nome>`; a branch principal é `main`.

### Java

- Pacote único `org.example`; uma classe pública por arquivo.
- Toda classe começa com o bloco Javadoc `@author Guilherme Lima Conte`.
- Classes em PascalCase e métodos em camelCase, ambos em português (`ConfigTarefas`, `adicionarTarefa`, `buscarTarefaId`).
- Os atributos de `Tarefa` são em inglês (`name`, `description`, `date`, `priority`, `category`, `status`) e os de `Alarme` em português (`prioridade`, `disparado`). Mantenha o padrão de cada classe; não renomeie.
- Atributos sempre `private`, expostos por getters/setters. Listas são devolvidas como cópia (`new ArrayList<>(...)`).
- Validação no construtor com `IllegalArgumentException` e mensagem em português.
- Filtros com `stream().filter(...).collect(Collectors.toList())`.
- Separação de responsabilidades: `Main` cuida só de entrada/saída (um método `private static` por opção do menu, lendo com `scanner.nextLine()` e convertendo); regras e estado ficam em `ConfigTarefas`; `Tarefa` e `Alarme` são modelos.

### Front-end

- Um único `main.js`, carregado por `<script>` no fim do `body`. Sem `import`/`export`.
- `const`/`let`, funções em camelCase e em português (`renderizarTarefas`, `verificarAlarmes`).
- Estado global em `listaTarefas`; depois de alterá-lo, chame `renderizarTarefas()`.
- Funções chamadas por `onclick` inline são atribuídas a `window` (`window.removerTarefa = function ...`).
- Renderização com `createElement` + template strings em `innerHTML`.
- Objetos de tarefa usam as mesmas chaves do modelo Java: `id`, `name`, `description`, `date`, `priority`, `category`, `status`, `alarmes`. Alarmes: `id`, `prioridade`, `disparado`.
- IDs e classes HTML/CSS em português, em sua maioria `snake_case` (`input_nome`, `cards_container`, `alarme_container`), com alguns em camelCase já existentes (`criarCardForm`, `buttonCriarCard`). Siga o padrão do elemento vizinho.
- Os `id` das colunas do kanban (`BACKLOG`, `TODO`, `DOING`, `DONE`) são iguais aos valores de `status`; `renderizarTarefas` depende disso.

## Restrições

Não faça nada disto sem pedir antes:

- Alterar ou remover funcionalidades existentes fora do escopo da feature solicitada, incluindo corrigir os pontos de atenção listados acima.
- Refatorar, reformatar, renomear ou traduzir código que não faz parte da tarefa.
- Mudar regras de domínio (faixa de prioridade, ordenação, valores de status, status inicial, regras de disparo ou tabela de antecedência dos alarmes) além do que está previsto em `docs/spec.md`.
- Renomear atributos de `Tarefa`/`Alarme` ou chaves dos objetos JSON/JS.
- Adicionar dependências, plugins Gradle, frameworks ou ferramentas de build (npm, bundlers, Spring etc.).
- Integrar o front-end ao back-end ou criar camada de persistência além do que a feature pedir.
- Mudar a estrutura de pastas ou o nome do pacote.
- Editar `tarefas.json`, os arquivos em `assets/`, o Gradle wrapper, o `.gitignore` ou qualquer coisa em `.idea/`.
- Fazer commit, push, criar/trocar de branch ou reescrever histórico.

Sempre:

- Mantenha a mudança mínima e restrita aos arquivos da feature.
- Rode `./gradlew build` depois de alterar código Java e informe o resultado.
- Ao mexer no front-end, diga o que foi verificado manualmente e o que não foi.
- Se encontrar um bug ou inconsistência fora do escopo, relate em vez de corrigir.
- Em caso de dúvida sobre o escopo, pergunte.
