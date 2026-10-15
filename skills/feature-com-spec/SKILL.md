---
name: feature-com-spec
description: Implementa uma feature pelo fluxo spec → plano → implementação (Spec-Driven Development), com AGENTS.md, docs/spec.md, docs/plano.md, tarefas pequenas e um commit por tarefa. Use sempre que o usuário pedir para implementar, adicionar ou desenvolver uma feature, funcionalidade ou requisito novo em um projeto existente, e também quando falar em SDD, especificação, spec, critérios de aceite, plano de implementação ou "implementação incremental", mesmo que não cite esta skill pelo nome. Não use para correções pontuais de bug, ajustes de uma linha ou perguntas sobre o código.
---

# Feature com spec

## Objetivo

Implementar uma feature seguindo o fluxo **spec → plano → implementação**: primeiro se decide e se escreve *o que* a feature faz, depois *como* ela será construída em passos pequenos, e só então se escreve código, um passo por vez.

A ordem existe por um motivo. Uma dúvida resolvida na spec custa uma pergunta; a mesma dúvida descoberta no meio do código custa retrabalho. E tarefas pequenas, validadas uma a uma, deixam o usuário no controle: ele sabe o que mudou, consegue revisar e pode parar ou redirecionar a qualquer momento.

## Entradas

1. **A descrição da feature:** o texto do usuário, com requisitos obrigatórios e exemplos de uso, se houver.
2. **O projeto onde ela será implementada:** o repositório aberto na sessão.

Se faltar uma das duas, peça antes de começar. A escolha da feature e do projeto é a etapa 1, que acontece antes desta skill; por isso o passo a passo começa na etapa 2.

## Passo a passo

Cada etapa termina com uma revisão do usuário. Não avance para a etapa seguinte sem a aprovação da anterior.

### Etapa 2: Contexto para o agente (`AGENTS.md`)

**O que gerar:** um `AGENTS.md` na raiz do projeto, escrito depois de explorar o repositório. Se o arquivo já existir, leia-o e atualize só o que estiver desatualizado.

Ele deve conter:

- **Visão geral:** o que a aplicação faz e quais funcionalidades já existem.
- **Stack e estrutura:** linguagens, ferramentas, versões e organização das pastas.
- **Como executar e testar:** os comandos para rodar a aplicação e os testes.
- **Convenções:** padrões de nomes, organização de classes ou módulos, estilo de código e de commit.
- **Restrições:** o que o agente não deve fazer sem pedir.

Como fazer bem:

- Leia o código, não só o README. O README pode estar errado ou desatualizado.
- Rode os comandos antes de documentá-los (build, execução, testes). Um comando que não foi executado é um palpite; se não der para rodar, diga isso no arquivo.
- Descreva o código como ele está hoje. O que ainda é só intenção vai para a spec, não para o `AGENTS.md`.
- Registre problemas encontrados como "pontos de atenção", sem corrigi-los.

**O que revisar:** os comandos funcionam como descrito; as convenções batem com o código real; as restrições cobrem o que o usuário não quer que seja mexido.

### Etapa 3: Especificação (`docs/spec.md`)

**O que gerar:** a especificação da feature, seguindo o modelo do fim deste documento. Nenhum código é escrito nesta etapa.

Como fazer bem:

1. **Pergunte antes de escrever.** Leia a descrição da feature e o código que ela toca, e levante as decisões que mudam o comportamento: dados iniciais, valores padrão, casos de erro, interação com o que já existe. Para cada uma, ofereça opções com a consequência de cada escolha e indique a que você recomenda. Não tire conclusões pelo usuário.
2. **Escreva a spec** com regras numeradas (`RN-01`) e critérios de aceite numerados (`CA-01`), para que o plano e os relatos possam citá-los.
3. **Separe o que foi decidido do que foi assumido.** Toda premissa que você adotou sem perguntar vai para uma lista "a confirmar". Quando o usuário confirmar, mova para as decisões.
4. **Procure o impacto no que já existe.** Leia o código das funcionalidades vizinhas e escreva o que muda nelas, inclusive efeitos colaterais que o usuário talvez não espere.

Se durante a spec aparecer um pré-requisito que é, na prática, outra feature, trate-o como uma feature separada: ele ganha a própria spec e é implementado antes.

**O que revisar:** as regras cobrem os casos de borda; cada critério de aceite é verificável por alguém que não escreveu a spec; o "fora do escopo" está explícito; não sobrou premissa sem confirmação. A spec só está aprovada quando o usuário disser isso.

### Etapa 4: Plano de implementação (`docs/plano.md`)

**O que gerar:** o plano em tarefas pequenas e numeradas, seguindo o modelo do fim deste documento. Cada tarefa indica:

- **O que será feito**, citando as regras da spec que ela atende.
- **Quais arquivos** serão criados ou alterados.
- **Como validar** que está pronta: qual teste roda e o que conferir manualmente.

Como fazer bem:

- **Decida os testes com o usuário** se o projeto não tiver um padrão: onde ficam, com que ferramenta rodam, se são escritos antes do código e em que formato (por exemplo, given / when / then). Não adicione dependência ou ferramenta sem perguntar.
- **Corte cada tarefa do tamanho de um commit.** Uma tarefa boa deixa o projeto funcionando ao terminar e pode ser validada sozinha.
- **Separe lógica de ligação.** Lógica isolada (cálculo, regra, leitura e gravação) é testável e vem primeiro, com teste. A ligação com tela, rede ou entrada do usuário costuma depender de conferência manual; diga isso na tarefa em vez de fingir cobertura.
- **Verifique os comandos de teste** rodando um exemplo descartável antes de escrevê-los no plano.
- **Confira o ponto de partida:** a branch está certa e contém o que a feature pressupõe?
- **Termine com uma tarefa de regressão e documentação.**

**O que revisar:** toda regra da spec aparece em alguma tarefa; nenhuma tarefa mexe em arquivo fora do escopo; a validação de cada tarefa é concreta. O plano só está aprovado quando o usuário disser isso.

### Etapa 5: Implementação incremental

**O que gerar:** o código de **uma tarefa por vez**, na ordem do plano.

Para cada tarefa:

1. **Confira o ponto de partida.** Veja o estado do repositório: a tarefa anterior já foi commitada? Se não, não misture as duas; avise e espere.
2. **Releia a tarefa no plano e as regras da spec que ela cita.**
3. **Se a tarefa tem teste, escreva o teste primeiro**, rode e veja falhar. Depois implemente e veja passar.
4. **Rode a suíte inteira**, não só o teste novo.
5. **Marque a situação da tarefa no plano:** concluída, ou implementada e aguardando a conferência manual do usuário.
6. **Relate**, citando a tarefa do plano e as regras da spec:
   - o que mudou e em quais arquivos;
   - o que você verificou e como;
   - o que falta o usuário conferir, em passos que ele possa seguir;
   - qualquer decisão que você tomou além do texto do plano.
7. **Pare.** Só comece a tarefa seguinte quando o usuário pedir.

"Conferi" ou "está funcionando" sobre uma tarefa não é pedido para começar a próxima: o usuário pode ainda querer revisar ou commitar. Na dúvida, pergunte.

Combine os commits no início da etapa: quem commita (o usuário ou o agente) e em que estilo de mensagem. Se for o agente, faça o commit só depois que o usuário validar a tarefa.

**O que revisar:** a cada tarefa, o usuário confere a validação descrita no plano e faz (ou autoriza) o commit.

### Etapa 6: Validação

**O que gerar:** a confirmação de que a feature atende à spec e não quebrou o que existia.

1. **Rode todos os testes** do projeto.
2. **Repasse os critérios de aceite** da spec, um por um, e diga como cada um foi verificado: por teste, manualmente ou ainda não verificado.
3. **Faça a regressão** das funcionalidades listadas em "Impacto no que já existe".
4. **Confirme o escopo:** compare com o commit de partida e mostre que nenhum arquivo fora do escopo foi alterado.
5. **Atualize a documentação:** o `AGENTS.md` passa a descrever a feature como existente; o README ganha a descrição de uso e de como rodar os testes; a spec é marcada como implementada.

**O que revisar:** o usuário faz a conferência final do que só ele consegue ver (tela, dispositivo, ambiente real) e lê a documentação atualizada.

## Regras

- **Não escreva código antes da spec aprovada.** Código escrito cedo vira argumento para não mudar a spec, e a decisão deixa de ser do usuário.
- **Uma tarefa por vez.** Implemente, relate e pare. Duas tarefas misturadas não podem ser revisadas nem commitadas separadamente.
- **Um commit por tarefa.** O histórico passa a contar a feature passo a passo, e qualquer tarefa pode ser revertida sozinha.
- **Não altere o que estiver fora do escopo.** Isso inclui corrigir bugs antigos, refatorar e reformatar. Se encontrar um problema fora do escopo, relate e deixe a decisão com o usuário.
- **Pergunte em vez de supor.** Quando uma decisão muda o comportamento da feature, ela é do usuário.
- **Relate com fidelidade.** Diga o que foi verificado e o que não foi. "Passou em uma simulação" não é "funciona no navegador"; se um teste falhou, mostre a saída.
- **Operações de git precisam de autorização:** commit, merge, push e troca de branch só com o aval do usuário.
- **Mantenha spec, plano e código de acordo.** Se uma decisão mudar no meio do caminho, atualize a spec e o plano antes de mudar o código.

## Modelos

Os caminhos padrão são `docs/spec.md` e `docs/plano.md`. Se o projeto já tiver esses arquivos para outra feature, pergunte ao usuário onde colocar os novos (por exemplo, `docs/<nome-da-feature>/`).

### `docs/spec.md`

```markdown
# Spec: <nome da feature>

Branch: `<branch>` · Escopo: <partes do projeto que a feature toca>

## 1. Contexto e objetivo

<Qual problema a feature resolve, para quem, e como é hoje sem ela.>
<Um caso de uso de referência, se o usuário deu um.>

## 2. Regras de negócio

**RN-01 — <título>.** <Como a feature deve se comportar.>
**RN-02 — <título>.** <...>

### Casos de borda

| Caso | Comportamento esperado |
| --- | --- |
| <situação incomum> | <o que acontece> |

## 3. Critérios de aceite

**CA-01 — <título>**
- Dado <estado inicial>
- Quando <ação>
- Então <resultado observável>

<Diga aqui como os critérios são verificados: teste automatizado, conferência manual ou ambos.>

## 4. Impacto no que já existe

| Funcionalidade atual | Impacto |
| --- | --- |
| <funcionalidade> | <"Sem impacto" ou o que muda, incluindo efeitos colaterais> |

## 5. Fora do escopo

Esta feature **não** vai:

- <o que fica de fora, inclusive correções tentadoras de problemas já conhecidos>

## 6. Decisões

Confirmadas pelo autor:

- <decisão>

Premissas assumidas na escrita, a confirmar:

- <premissa>
```

### `docs/plano.md`

```markdown
# Plano de implementação: <nome da feature>

Implementa `docs/spec.md`. Os códigos `RN-xx` e `CA-xx` são as regras e os critérios de aceite da spec.

## Decisões de implementação

| Tema | Decisão |
| --- | --- |
| Testes | <ferramenta, onde ficam, o que cobrem> |
| Organização do código | <arquivos, classes ou módulos novos> |
| Ordem | <por exemplo: teste antes do código> |
| Commits | <quem commita e em que estilo> |

## Estrutura ao final

<Árvore dos arquivos criados e alterados.>

## Como rodar os testes

<Comando exato, já verificado.>

<O que os testes cobrem e o que depende de conferência manual.>

## Tarefas

### 1. <título da tarefa>

- **O que:** <o que será feito> (RN-xx).
- **Arquivos:** <criados e alterados>.
- **Testes (escritos antes):**
  - given <estado>, when <ação>, then <resultado>;
- **Validação:** <qual teste roda e o que conferir manualmente, citando CA-xx>.
- **Situação:** <pendente | concluída | implementada, aguardando conferência do autor>.

### 2. <título da tarefa>

<...>

### N. Regressão e documentação

- **O que:** atualizar `AGENTS.md`, README e a situação da spec.
- **Arquivos:** <documentos>.
- **Validação:**
  - todos os testes passam;
  - todos os critérios de aceite repassados;
  - funcionalidades listadas em "Impacto" conferidas;
  - nenhum arquivo fora do escopo alterado.

## Pendências

- <o que depende de decisão ou autorização do autor>
```
