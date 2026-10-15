package org.example

import spock.lang.Specification

import java.time.LocalDateTime

/**
 *
 * @author Guilherme Lima Conte
 */

class AlarmeSpec extends Specification {

    def "criar alarme com prioridade válida"() {
        given: "uma prioridade entre 1 e 5"
        int prioridade = 3

        when: "cria o alarme"
        def alarme = new Alarme(prioridade)

        then: "guarda a prioridade e começa não disparado"
        alarme.prioridade == 3
        !alarme.disparado
    }

    def "criar alarme com prioridade #prioridade lança erro"() {
        given: "uma prioridade fora de 1 a 5, vinda da tabela do where"

        when: "cria o alarme"
        new Alarme(prioridade)

        then: "lança IllegalArgumentException com a mensagem da regra"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Prioridade do alarme deve ser entre 1 e 5"

        where:
        prioridade << [0, 6, -1]
    }

    def "marcar alarme como disparado"() {
        given: "um alarme novo"
        def alarme = new Alarme(1)

        when: "marca como disparado"
        alarme.disparado = true

        then: "isDisparado devolve true"
        alarme.disparado
    }

    def "alarme de prioridade #prioridade avisa #horas horas antes"() {
        given: "um alarme da prioridade"
        def alarme = new Alarme(prioridade)

        when: "consulta a antecedência"
        def antecedencia = alarme.horasAntecedencia

        then: "segue a tabela 2, 4, 6, 12 e 24 horas"
        antecedencia == horas

        where:
        prioridade | horas
        1          | 2
        2          | 4
        3          | 6
        4          | 12
        5          | 24
    }

    def "calcular horário do alarme a partir do vencimento"() {
        given: "uma tarefa que vence às 18h e um alarme de prioridade 4"
        def tarefa = new Tarefa("Relatório", "Entregar relatório",
                LocalDateTime.of(2030, 1, 10, 18, 0), 3 as byte, "Trabalho")
        def alarme = new Alarme(4)

        when: "calcula o horário"
        def horario = alarme.calcularHorario(tarefa)

        then: "devolve 12 horas antes do vencimento"
        horario == LocalDateTime.of(2030, 1, 10, 6, 0)
    }
}
