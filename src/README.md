Autor: Guilherme Lima Conte

Descrição:

Implementação do back-end do projeto TODO List.

**Tecnologias utilizadas:**

Java
Gradle como build tool
IntelliJ IDEA como IDE de desenvolvimento
Bibliotecas: java.util e java.time para manipular datas.

**Funcionalidades:**

Adicionar tarefa passando os parâmetros nome, descrição, data de vencimento, prioridade de 1 a 5, categoria
Listar todas as tarefas
Remover tarefa por ID
Atualizar o status de uma tarefa (BACKLOG, TODO, DOING ou DONE)
Toda tarefa nova começa no status BACKLOG
Filtro de tarefas por categoria prioridade e status
Rebalanceamento ao inserir uma nova tarefa, ordenando a lista por prioridade

Front-end:
Foi implementado um quadro kanban onde você pode organizar as tarefas pelo status BACKLOG, TODO, DOING, DONE. Toda tarefa nova começa no BACKLOG. É possível visualizar, criar e excluir uma tarefa. Todas as tarefas são ordenadas com base em seu nível de prioridade que vai de 1 até 5, sendo 5 o com a maior prioridade.
Estabeleci um banco de dados json apenas para ter algumas informações pré-estabelecidas durante o desenvolvimento e produção.

**Como executar:**

1. Clone o repositório:
   git clone git@github.com:Caduqui/TODOList.git

2. Localize o arquivo Main.java em `src/main/org.example`

3. Você pode clicar no ícone de play ao lado do método main ou pressionar shift + F10

Caso queira compilar e executar pelo terminal:

cd src/main/org.example
javac *.java
java Main

**Alarme**

Cada alarme possui uma prioridade própria que condiz com seu tempo de antecedência para realizar os alertas.

No back-end foi criado a classe alarme que é responsável por armazenar a prioridade do alarme, identificar se ele já foi disparado e calcular sua antecedência. O gerenciamento do disparado utiliza ScheludedExecutorService, permitindo o "efeito" do alarme enquanto o programa roda normalmente.

Tarefas com status BACKLOG ou DONE não disparam alarme. Quando a tarefa sai do BACKLOG, os alarmes que ficaram pendentes voltam a valer.

No front-end cada tarefa possui um ícone para clicar no alarme e exibir uma caixa onde pode adicionar um novo alarme, verificar a lista de alarmes já adicionados e excluir alarmes.

**Persistência no local storage**

As tarefas e os alarmes do front-end ficam gravados no local storage do navegador, na chave todolist.tarefas. Toda alteração (criar, editar ou remover tarefa, mudar status, adicionar ou excluir alarme) é gravada na hora, e ao abrir a página o quadro volta como foi deixado.

O arquivo tarefas.json só é carregado na primeira visita. Para voltar aos dados iniciais, apague a chave todolist.tarefas nas ferramentas do navegador e recarregue a página.

Um alarme que já avisou não avisa de novo depois de recarregar a página. Se o navegador não conseguir gravar, o quadro continua funcionando e mostra um aviso de que as alterações não estão sendo salvas.

**Mudança de status de várias tarefas**

Cada card tem uma caixa de seleção. Marque as tarefas, de qualquer coluna, escolha o status em "Mover para" e clique em "Executar": todas as marcadas mudam de status de uma vez, e uma mensagem informa quantas foram movidas.

**Testes**

Os testes unitários do front-end ficam em `src/test/javascript/front_end` e usam o executor de testes que já vem no Node, sem instalar nada. Cada teste segue o formato given / when / then.

Para rodar, a partir da raiz do projeto:

node --test "src/test/javascript/**/*.test.js"

**Comentários sobre a solução:**

Utilizei todos os atributos privados para maior prática dos métodos getters e setters, por estar utilizando tipos genéricos me aprovetei para usar o for-each e deixando a estrutura de repetição muito mais limpa.
Tive que utilizar localDate e DateTimeFormatter para formatar a data para o nosso tipo dd/mm/aaaa.
Para os filtros por categoria, status e prioridade utilizei da biblioteca stream.Collectors