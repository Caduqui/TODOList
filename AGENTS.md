# AGENTS.md

Guia para agentes de código que trabalham neste repositório. Leia antes de alterar qualquer arquivo.

## Visão geral

TODOList é um gerenciador de tarefas com duas partes **independentes** (não há integração entre elas):

- **Back-end (Java, CLI):** menu interativo no terminal. Os dados ficam só em memória e se perdem ao encerrar.
- **Front-end (HTML/CSS/JS puro):** quadro kanban com colunas `BACKLOG`, `TODO`, `DOING` e `DONE`. As tarefas e seus alarmes ficam gravados no local storage do navegador; o `tarefas.json` só é lido na primeira visita.

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
| Persistência das tarefas e dos alarmes (local storage) | não | sim |
| Mudar o status de várias tarefas de uma vez | não | sim |

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

### Regras do front-end: persistência e status em massa

- **Onde grava:** a lista inteira de tarefas, com seus alarmes, vai como array JSON para a chave `todolist.tarefas` do local storage.
- **Quando grava:** a cada alteração, sem botão de salvar: criar, editar ou remover tarefa, mudar status em massa, adicionar ou excluir alarme e disparo de alarme.
- **Carga ao abrir:** chave com um array válido, mesmo vazio, é usada como está e o seed não é lido. Chave ausente ou com conteúdo inválido carrega o `tarefas.json` e grava. Quadro vazio é um estado válido: o seed não volta sozinho.
- **Alarmes:** o campo `disparado` é persistido, então no front-end um alarme avisa uma única vez, mesmo depois de recarregar.
- **Falha ao gravar:** a página segue funcionando em memória e mostra o aviso `#aviso_storage`.
- **Status em massa:** o usuário marca os cards pelo checkbox, escolhe o destino em "Mover para" e clica em "Executar". Só muda o status de quem difere do destino, e nenhum outro campo é alterado. A seleção vale enquanto a página está aberta, não é gravada e é limpa depois de executar.

A especificação completa está em `docs/spec.md` e o plano de implementação em `docs/plano.md`.

## Stack e estrutura

- **Java 25** (JDK configurado no IntelliJ), sem dependências de runtime.
- **Gradle 9.6.0** via wrapper, apenas com o plugin `java` (não há plugin `application`).
- **JUnit Jupiter** (BOM 6.0.0) já configurado em `build.gradle`, mas ainda sem testes.
- **Front-end:** HTML, CSS e JavaScript puros. Sem npm, sem bundler, sem framework, sem módulos ES.
- **Testes de front-end:** executor de testes que já vem no Node (`node --test`), sem dependências. Verificado com o Node 24.
- IDE: IntelliJ IDEA.

```
TODOList/
├── build.gradle, settings.gradle     # build Gradle (Groovy DSL)
├── gradlew, gradlew.bat, gradle/     # wrapper
├── docs/
│   ├── spec.md                       # especificação: coluna BACKLOG (Parte A) e local storage + status em massa (Parte B)
│   └── plano.md                      # plano de implementação da Parte B, em tarefas
└── src/
    ├── README.md                     # README do projeto (fica em src/, não na raiz)
    ├── main/
    │   ├── java/org/example/
    │   │   ├── Main.java             # menu do CLI e leitura de entrada (Scanner)
    │   │   ├── ConfigTarefas.java    # lista de tarefas, filtros, ordenação e agendamento de alarmes
    │   │   ├── Tarefa.java           # modelo + enum TarefaStatus
    │   │   └── Alarme.java           # modelo do alarme e cálculo de antecedência
    │   └── javascript/front_end/
    │       ├── index.html                # kanban, barra de ação em massa, formulário de tarefa e modal de alarme
    │       ├── main.js                   # tela: desenho dos cards, eventos, alarmes e ligação com as classes
    │       ├── ArmazenamentoTarefas.js   # leitura e gravação da lista no local storage
    │       ├── SelecaoTarefas.js         # ids das tarefas marcadas e texto do contador
    │       ├── MudancaStatusEmMassa.js   # troca de status das marcadas e mensagem de resumo
    │       ├── style.css
    │       ├── tarefas.json              # dados iniciais (seed), lidos só na primeira visita
    │       └── assets/                   # ícones PNG (editar, lixeira, alarme)
    └── test/javascript/front_end/        # testes unitários das classes do front-end
        ├── ArmazenamentoTarefas.test.js
        ├── SelecaoTarefas.test.js
        └── MudancaStatusEmMassa.test.js
```

`src/test/java/` ainda não existe: o back-end não tem testes.

## Como executar e testar

Comandos a partir da raiz do repositório. No PowerShell use `.\gradlew.bat`; no bash, `./gradlew`.

```bash
# compilar (também roda os testes)
./gradlew build

# executar o CLI (não existe a task `gradlew run`)
java -cp build/classes/java/main org.example.Main

# rodar apenas os testes Java
./gradlew test

# rodar os testes do front-end
node --test "src/test/javascript/**/*.test.js"
```

- Pelo IntelliJ: abrir `Main.java` e executar o método `main` (Shift+F10).
- **Testes Java:** hoje `./gradlew test` termina com `NO-SOURCE`. Testes novos vão em `src/test/java/org/example/`, com JUnit Jupiter.
- **Testes de front-end:** testes unitários cobrem as três classes (`ArmazenamentoTarefas`, `SelecaoTarefas`, `MudancaStatusEmMassa`). O padrão precisa ir entre aspas; passar só a pasta (`node --test src/test/javascript/`) não funciona no Node 24. O `main.js`, que liga as classes à tela, não tem teste automatizado: a verificação dele é manual no navegador.
- **Front-end:** precisa ser servido por HTTP, porque `main.js` usa `fetch("tarefas.json")` e isso falha ao abrir o `index.html` direto do disco (`file://`). Use o servidor embutido do IntelliJ (botão de abrir no navegador sobre o `index.html`) ou qualquer servidor estático apontando para `src/main/javascript/front_end/`.
- **Dados no navegador:** para conferir o que está gravado, abra DevTools → Application → Local Storage. Para voltar ao seed, apague a chave `todolist.tarefas` e recarregue; alterar o `tarefas.json` não reflete em um navegador que já tem a chave.

### Pontos de atenção conhecidos

- O `src/README.md` indica `cd src/main/org.example`; o caminho real é `src/main/java/org/example`.
- No CLI, depois que um alarme é agendado, a opção `9- Sair` não encerra o processo: a thread do `ScheduledExecutorService` em `ConfigTarefas` segue viva.
- Formatos de data diferentes: o CLI lê `dd/MM/yyyy HH:mm`; o formulário do front usa `datetime-local` (`yyyy-MM-ddTHH:mm`); o seed em `tarefas.json` traz só a data (`yyyy-MM-dd`).
- IDs: no Java vêm de um contador estático em `Tarefa`; no front, de `Date.now()`.
- O texto das tarefas é inserido no HTML sem escape (`innerHTML`). Com a persistência, um conteúdo malformado digitado em uma tarefa volta a cada abertura da página.
- No front-end, um alarme já disparado não é rearmado quando a data da tarefa é editada.
- Abas abertas ao mesmo tempo não se sincronizam: vale a última gravação no local storage.

Esses pontos são registro de contexto, não tarefas. Não os corrija fora do escopo pedido.

## Convenções

### Geral

- Código, mensagens ao usuário e commits em **português**.
- Indentação de 4 espaços em Java, JS, HTML e CSS.
- Commits curtos e descritivos, um por tarefa. Os mais recentes usam o prefixo `feat:` (`feat: adiciona classe SelecaoTarefas`); os mais antigos não têm prefixo (`Implementação do alarme`). Branches de feature como `feature/<nome>`; a branch principal é `main`.

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

- Sem `import`/`export`: os arquivos são carregados por `<script>` no fim do `body`, com as classes antes do `main.js`.
- Lógica que não depende da tela fica em classes, uma por arquivo, com o arquivo nomeado como a classe, em PascalCase e em português (`ArmazenamentoTarefas.js`). Métodos em camelCase e em português.
- Cada arquivo de classe termina com `if (typeof module !== "undefined") { module.exports = { NomeDaClasse }; }`. O trecho só tem efeito no Node e permite o `require` nos testes.
- As classes não acessam o DOM. O que vem de fora, como o storage, entra pelo construtor, para o teste poder passar um falso.
- O `main.js` cuida da tela (DOM, eventos, alarmes) e usa as classes.
- `const`/`let`, funções em camelCase e em português (`renderizarTarefas`, `verificarAlarmes`).
- Estado global em `listaTarefas`; depois de alterá-lo, chame `salvarTarefas()` e `renderizarTarefas()`. Uma alteração que não passa por `salvarTarefas()` se perde na recarga.
- Funções chamadas por `onclick` inline são atribuídas a `window` (`window.removerTarefa = function ...`).
- Renderização com `createElement` + template strings em `innerHTML`.
- Objetos de tarefa usam as mesmas chaves do modelo Java: `id`, `name`, `description`, `date`, `priority`, `category`, `status`, `alarmes`. Alarmes: `id`, `prioridade`, `disparado`.
- IDs e classes HTML/CSS em português, em sua maioria `snake_case` (`input_nome`, `cards_container`, `alarme_container`), com alguns em camelCase já existentes (`criarCardForm`, `buttonCriarCard`). Siga o padrão do elemento vizinho.
- Os `id` das colunas do kanban (`BACKLOG`, `TODO`, `DOING`, `DONE`) são iguais aos valores de `status`; `renderizarTarefas` depende disso.

### Testes de front-end

- Um arquivo `NomeDaClasse.test.js` por classe, em `src/test/javascript/front_end/`, usando `node:test` e `node:assert/strict`.
- Um `describe` para a classe e outro para cada método.
- Nome de cada teste no formato `given ..., when ..., then ...`, e corpo dividido pelos comentários `// given`, `// when` e `// then`.
- Teste antes do código: escrever o teste, vê-lo falhar, implementar, vê-lo passar.

## Restrições

Não faça nada disto sem pedir antes:

- Alterar ou remover funcionalidades existentes fora do escopo da feature solicitada, incluindo corrigir os pontos de atenção listados acima.
- Refatorar, reformatar, renomear ou traduzir código que não faz parte da tarefa.
- Mudar regras de domínio (faixa de prioridade, ordenação, valores de status, status inicial, regras de disparo ou tabela de antecedência dos alarmes) além do que está previsto em `docs/spec.md`.
- Renomear atributos de `Tarefa`/`Alarme` ou chaves dos objetos JSON/JS.
- Renomear a chave `todolist.tarefas` ou mudar o formato do que é gravado nela: isso inutiliza os dados já salvos nos navegadores.
- Adicionar dependências, plugins Gradle, frameworks ou ferramentas de build (npm, bundlers, Spring etc.).
- Integrar o front-end ao back-end ou criar camada de persistência além do que a feature pedir.
- Mudar a estrutura de pastas ou o nome do pacote.
- Editar `tarefas.json`, os arquivos em `assets/`, o Gradle wrapper, o `.gitignore` ou qualquer coisa em `.idea/`.
- Fazer commit, push, criar/trocar de branch ou reescrever histórico.

Sempre:

- Mantenha a mudança mínima e restrita aos arquivos da feature.
- Rode `./gradlew build` depois de alterar código Java e informe o resultado.
- Rode `node --test "src/test/javascript/**/*.test.js"` depois de alterar o front-end e informe o resultado.
- Ao mexer no front-end, diga o que foi verificado manualmente e o que não foi.
- Se encontrar um bug ou inconsistência fora do escopo, relate em vez de corrigir.
- Em caso de dúvida sobre o escopo, pergunte.
