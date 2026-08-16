autor: Guilherme Lima Conte

Descrição:

Implementação do back-end do projeto TODO List.

Tecnologias utilizadas:

Java
Gradle como build tool
IntelliJ IDEA como IDE de desenvolvimento
Bibliotecas: java.util e java.time para manipular datas.

Funcionalidades:

Adicionar tarefa passando os parâmetros nome, descrição, data de vencimento, prioridade de 1 a 5, categoria
Listar todas as tarefas
Remover tarefa por ID
Atualizar o status de uma tarefa (TODO, DOING ou DONE)
Filtro de tarefas por categoria prioridade e status
Rebalanceamento ao inserir uma nova tarefa, ordenando a lista por prioridade

Front-end:
Foi implementado um quadro kanban onde você pode organizar as tarefas pelo status TODO, DOING, DONE. É possível visualizar, criar e excluir uma tarefa. Todas as tarefas são ordenadas com base em seu nível de prioridade que vai de 1 até 5, sendo 5 o com a maior prioridade.
Estabeleci um banco de dados json apenas para ter algumas informações pré-estabelecidas durante o desenvolvimento e produção.

Como executar:

1. Clone o repositório:
   git clone git@github.com:Caduqui/TODOList.git

2. Localize o arquivo Main.java em src/main/org.example

3. Você pode clicar no ícone de play ao lado do método main ou pressionar shift + F10

Caso queira compilar e executar pelo terminal:

cd src/main/org.example
javac *.java
java Main

Comentários sobre a solução:

Utilizei todos os atributos privados para maior prática dos métodos getters e setters, por estar utilizando tipos genéricos me aprovetei para usar o for-each e deixando a estrutura de repetição muito mais limpa.
Tive que utilizar localDate e DateTimeFormatter para formatar a data para o nosso tipo dd/mm/aaaa.
Para os filtros por categoria, status e prioridade utilizei da biblioteca stream.Collectors