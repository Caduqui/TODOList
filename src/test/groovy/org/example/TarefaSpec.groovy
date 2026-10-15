package org.example

import spock.lang.Specification

import java.time.LocalDateTime

/**
 *
 * @author Guilherme Lima Conte
 */

class TarefaSpec extends Specification {

    static final LocalDateTime VENCIMENTO = LocalDateTime.of(2030, 1, 10, 18, 0)

    private static Tarefa novaTarefa() {
        new Tarefa("Estudar", "Estudar Spock", VENCIMENTO, 3 as byte, "Estudos")
    }

    def "criar tarefa com dados válidos"() {
        given: "dados válidos de uma tarefa"
        def name = "Estudar"
        def description = "Estudar Spock"
        byte priority = 3
        def category = "Estudos"

        when: "cria a tarefa"
        def tarefa = new Tarefa(name, description, VENCIMENTO, priority, category)

        then: "guarda os campos, começa em BACKLOG e sem alarmes"
        tarefa.name == "Estudar"
        tarefa.description == "Estudar Spock"
        tarefa.date == VENCIMENTO
        tarefa.priority == 3
        tarefa.category == "Estudos"
        tarefa.status == Tarefa.TarefaStatus.BACKLOG
        tarefa.alarmes.isEmpty()
    }

    def "cada tarefa nova recebe o id seguinte"() {
        given: "uma tarefa já criada"
        def primeira = novaTarefa()

        when: "cria outra tarefa"
        def segunda = novaTarefa()

        then: "a nova recebe o id seguinte"
        segunda.id == primeira.id + 1
    }

    def "criar tarefa com prioridade #priority lança erro"() {
        given: "uma prioridade fora de 1 a 5, vinda da tabela do where"

        when: "cria a tarefa"
        new Tarefa("Estudar", "Estudar Spock", VENCIMENTO, priority as byte, "Estudos")

        then: "lança IllegalArgumentException com a mensagem da regra"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Prioridade deve ser entre 1 e 5"

        where:
        priority << [0, 6, -1]
    }

    def "mudar o status da tarefa"() {
        given: "uma tarefa em BACKLOG"
        def tarefa = novaTarefa()

        when: "muda o status para DOING"
        tarefa.status = Tarefa.TarefaStatus.DOING

        then: "getStatus devolve DOING"
        tarefa.status == Tarefa.TarefaStatus.DOING
    }

    def "trocar o nome da tarefa"() {
        given: "uma tarefa"
        def tarefa = novaTarefa()

        when: "troca o nome"
        tarefa.name = "Estudar Groovy"

        then: "getName devolve o nome novo"
        tarefa.name == "Estudar Groovy"
    }

    def "trocar a descrição da tarefa"() {
        given: "uma tarefa"
        def tarefa = novaTarefa()

        when: "troca a descrição"
        tarefa.description = "Ler a documentação do Spock"

        then: "getDescription devolve a descrição nova"
        tarefa.description == "Ler a documentação do Spock"
    }

    def "trocar o vencimento da tarefa"() {
        given: "uma tarefa e um vencimento novo"
        def tarefa = novaTarefa()
        def novoVencimento = LocalDateTime.of(2031, 2, 1, 9, 30)

        when: "troca o vencimento"
        tarefa.date = novoVencimento

        then: "getDate devolve o vencimento novo"
        tarefa.date == novoVencimento
    }

    def "trocar a categoria da tarefa"() {
        given: "uma tarefa"
        def tarefa = novaTarefa()

        when: "troca a categoria"
        tarefa.category = "Trabalho"

        then: "getCategory devolve a categoria nova"
        tarefa.category == "Trabalho"
    }

    def "trocar a prioridade da tarefa"() {
        given: "uma tarefa de prioridade 3"
        def tarefa = novaTarefa()

        when: "troca a prioridade para 5"
        tarefa.priority = 5 as byte

        then: "getPriority devolve 5"
        tarefa.priority == 5
    }

    def "trocar a prioridade para #priority lança erro e mantém a anterior"() {
        given: "uma tarefa de prioridade 3"
        def tarefa = novaTarefa()

        when: "troca para uma prioridade fora de 1 a 5"
        tarefa.priority = priority as byte

        then: "lança IllegalArgumentException e a prioridade continua 3"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Prioridade deve ser entre 1 e 5"
        tarefa.priority == 3

        where:
        priority << [0, 6]
    }

    def "adicionar alarme à tarefa"() {
        given: "uma tarefa sem alarmes e um alarme"
        def tarefa = novaTarefa()
        def alarme = new Alarme(2)

        when: "adiciona o alarme"
        tarefa.adicionarAlarme(alarme)

        then: "ele aparece na lista de alarmes"
        tarefa.alarmes == [alarme]
    }

    def "a lista de alarmes devolvida é uma cópia"() {
        given: "uma tarefa sem alarmes"
        def tarefa = novaTarefa()

        when: "altera a lista devolvida"
        tarefa.alarmes.add(new Alarme(1))

        then: "os alarmes da tarefa não mudam"
        tarefa.alarmes.isEmpty()
    }
}
