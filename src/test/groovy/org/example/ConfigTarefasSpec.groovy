package org.example

import spock.lang.Specification

import java.time.LocalDateTime

/**
 *
 * @author Guilherme Lima Conte
 */

class ConfigTarefasSpec extends Specification {

    static final LocalDateTime VENCIMENTO = LocalDateTime.of(2030, 1, 10, 18, 0)

    ConfigTarefas configTarefas = new ConfigTarefas()

    private static Tarefa novaTarefa(String name, int priority, String category) {
        new Tarefa(name, "Descrição de " + name, VENCIMENTO, priority as byte, category)
    }

    def "adicionar tarefa numa lista vazia"() {
        given: "uma lista vazia e uma tarefa"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")

        when: "adiciona a tarefa"
        configTarefas.adicionarTarefa(tarefa)

        then: "a lista passa a ter só essa tarefa"
        configTarefas.todasTarefas == [tarefa]
    }

    def "adicionar tarefas fora de ordem deixa a lista ordenada por prioridade"() {
        given: "tarefas de prioridades 1, 5 e 3"
        def baixa = novaTarefa("Baixa", 1, "Casa")
        def alta = novaTarefa("Alta", 5, "Casa")
        def media = novaTarefa("Média", 3, "Casa")

        when: "adiciona nessa ordem"
        configTarefas.adicionarTarefa(baixa)
        configTarefas.adicionarTarefa(alta)
        configTarefas.adicionarTarefa(media)

        then: "a lista fica em ordem decrescente de prioridade"
        configTarefas.todasTarefas*.name == ["Alta", "Média", "Baixa"]
    }

    def "listar tarefas de uma lista vazia"() {
        given: "uma lista vazia"

        when: "lista as tarefas"
        def tarefas = configTarefas.todasTarefas

        then: "devolve uma lista vazia"
        tarefas.isEmpty()
    }

    def "a lista de tarefas devolvida é uma cópia"() {
        given: "uma tarefa cadastrada"
        configTarefas.adicionarTarefa(novaTarefa("Estudar", 3, "Estudos"))

        when: "altera a lista devolvida"
        configTarefas.todasTarefas.clear()

        then: "as tarefas cadastradas não mudam"
        configTarefas.todasTarefas.size() == 1
    }

    def "buscar tarefa pelo id"() {
        given: "uma tarefa cadastrada"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)

        when: "busca pelo id dela"
        def encontrada = configTarefas.buscarTarefaId(tarefa.id)

        then: "devolve essa tarefa"
        encontrada.is(tarefa)
    }

    def "buscar tarefa por id que não existe"() {
        given: "uma tarefa cadastrada"
        configTarefas.adicionarTarefa(novaTarefa("Estudar", 3, "Estudos"))

        when: "busca por um id que não existe"
        def encontrada = configTarefas.buscarTarefaId(-1)

        then: "devolve null"
        encontrada == null
    }

    def "filtrar por categoria ignorando maiúsculas e minúsculas"() {
        given: "tarefas de categorias diferentes"
        configTarefas.adicionarTarefa(novaTarefa("Estudar", 3, "Estudos"))
        configTarefas.adicionarTarefa(novaTarefa("Lavar louça", 2, "Casa"))
        configTarefas.adicionarTarefa(novaTarefa("Revisar", 1, "Estudos"))

        when: "filtra por ESTUDOS"
        def filtradas = configTarefas.getTarefasCategoria("ESTUDOS")

        then: "devolve só as da categoria Estudos"
        filtradas*.name == ["Estudar", "Revisar"]
    }

    def "filtrar por prioridade"() {
        given: "tarefas de prioridades 5 e 1"
        configTarefas.adicionarTarefa(novaTarefa("Urgente", 5, "Trabalho"))
        configTarefas.adicionarTarefa(novaTarefa("Tranquila", 1, "Trabalho"))

        when: "filtra pela prioridade 5"
        def filtradas = configTarefas.getTarefasPrioridade(5)

        then: "devolve só as de prioridade 5"
        filtradas*.name == ["Urgente"]
    }

    def "filtrar por status"() {
        given: "uma tarefa em DOING e outra em BACKLOG"
        def fazendo = novaTarefa("Fazendo", 3, "Trabalho")
        configTarefas.adicionarTarefa(fazendo)
        configTarefas.adicionarTarefa(novaTarefa("Parada", 3, "Trabalho"))
        configTarefas.atualizarStatus(fazendo.id, Tarefa.TarefaStatus.DOING)

        when: "filtra por DOING"
        def filtradas = configTarefas.getTarefasStatus(Tarefa.TarefaStatus.DOING)

        then: "devolve só a que está em DOING"
        filtradas == [fazendo]
    }

    def "atualizar o status de uma tarefa"() {
        given: "uma tarefa em BACKLOG"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)

        when: "atualiza o status para TODO"
        def atualizou = configTarefas.atualizarStatus(tarefa.id, Tarefa.TarefaStatus.TODO)

        then: "devolve true e a tarefa fica em TODO"
        atualizou
        tarefa.status == Tarefa.TarefaStatus.TODO
    }

    def "atualizar o status de um id que não existe"() {
        given: "uma tarefa cadastrada"
        configTarefas.adicionarTarefa(novaTarefa("Estudar", 3, "Estudos"))

        when: "atualiza o status de um id que não existe"
        def atualizou = configTarefas.atualizarStatus(-1, Tarefa.TarefaStatus.DONE)

        then: "devolve false"
        !atualizou
    }

    def "tarefa que sai do BACKLOG dispara o alarme pendente"() {
        given: "uma tarefa em BACKLOG que vence em 1 hora, com alarme de 2 horas de antecedência"
        def tarefa = new Tarefa("Pagar conta", "Boleto", LocalDateTime.now().plusHours(1), 3 as byte, "Casa")
        configTarefas.adicionarTarefa(tarefa)
        def alarme = new Alarme(1)
        configTarefas.adicionarAlarme(tarefa, alarme)

        when: "a tarefa sai do BACKLOG"
        configTarefas.atualizarStatus(tarefa.id, Tarefa.TarefaStatus.TODO)

        then: "o alarme dispara"
        alarme.disparado
    }

    def "editar todos os campos de uma tarefa"() {
        given: "uma tarefa cadastrada e um vencimento novo"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)
        def novoVencimento = LocalDateTime.of(2031, 2, 1, 9, 30)

        when: "edita todos os campos"
        def editou = configTarefas.editarTarefa(tarefa.id, "Trabalhar", "Fechar a sprint",
                novoVencimento, 4 as byte, "Trabalho")

        then: "devolve true e a tarefa fica com os valores novos"
        editou
        tarefa.name == "Trabalhar"
        tarefa.description == "Fechar a sprint"
        tarefa.date == novoVencimento
        tarefa.priority == 4
        tarefa.category == "Trabalho"
    }

    def "editar uma tarefa mantém o id, o status e os alarmes"() {
        given: "uma tarefa em DOING com alarme"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)
        configTarefas.atualizarStatus(tarefa.id, Tarefa.TarefaStatus.DOING)
        def alarme = new Alarme(5)
        tarefa.adicionarAlarme(alarme)
        def id = tarefa.id

        when: "edita a tarefa"
        configTarefas.editarTarefa(id, "Estudar mais", "Outra descrição",
                VENCIMENTO.plusDays(1), 2 as byte, "Estudos")

        then: "o id, o status e os alarmes continuam os mesmos"
        tarefa.id == id
        tarefa.status == Tarefa.TarefaStatus.DOING
        tarefa.alarmes == [alarme]
    }

    def "editar a prioridade reordena a lista"() {
        given: "tarefas de prioridades 4 e 1"
        def alta = novaTarefa("Alta", 4, "Casa")
        def baixa = novaTarefa("Baixa", 1, "Casa")
        configTarefas.adicionarTarefa(alta)
        configTarefas.adicionarTarefa(baixa)

        when: "a de prioridade 1 passa a 5"
        configTarefas.editarTarefa(baixa.id, "Baixa", baixa.description,
                baixa.date, 5 as byte, "Casa")

        then: "ela vai para o início da lista"
        configTarefas.todasTarefas*.name == ["Baixa", "Alta"]
    }

    def "editar um id que não existe"() {
        given: "uma tarefa cadastrada"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)

        when: "edita um id que não existe"
        def editou = configTarefas.editarTarefa(-1, "Outro", "Outra", VENCIMENTO, 1 as byte, "Outra")

        then: "devolve false e nenhuma tarefa muda"
        !editou
        tarefa.name == "Estudar"
    }

    def "editar com prioridade inválida não altera nenhum campo"() {
        given: "uma tarefa cadastrada"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)

        when: "edita com prioridade 6"
        configTarefas.editarTarefa(tarefa.id, "Trabalhar", "Fechar a sprint",
                VENCIMENTO.plusDays(1), 6 as byte, "Trabalho")

        then: "lança IllegalArgumentException e nenhum campo muda"
        thrown(IllegalArgumentException)
        tarefa.name == "Estudar"
        tarefa.description == "Descrição de Estudar"
        tarefa.date == VENCIMENTO
        tarefa.priority == 3
        tarefa.category == "Estudos"
    }

    def "editar o vencimento não rearma alarme já disparado"() {
        given: "uma tarefa com alarme já disparado"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)
        def alarme = new Alarme(1)
        alarme.disparado = true
        tarefa.adicionarAlarme(alarme)

        when: "edita o vencimento"
        configTarefas.editarTarefa(tarefa.id, "Estudar", tarefa.description,
                VENCIMENTO.plusDays(7), 3 as byte, "Estudos")

        then: "o alarme continua disparado"
        alarme.disparado
    }

    def "remover tarefa pelo id"() {
        given: "duas tarefas cadastradas"
        def fica = novaTarefa("Fica", 3, "Casa")
        def sai = novaTarefa("Sai", 2, "Casa")
        configTarefas.adicionarTarefa(fica)
        configTarefas.adicionarTarefa(sai)

        when: "remove uma pelo id"
        def removeu = configTarefas.removerTarefa(sai.id)

        then: "devolve true e só a outra continua na lista"
        removeu
        configTarefas.todasTarefas == [fica]
    }

    def "remover tarefa por id que não existe"() {
        given: "uma tarefa cadastrada"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        configTarefas.adicionarTarefa(tarefa)

        when: "remove um id que não existe"
        def removeu = configTarefas.removerTarefa(-1)

        then: "devolve false e a lista não muda"
        !removeu
        configTarefas.todasTarefas == [tarefa]
    }

    def "adicionar alarme a uma tarefa com vencimento distante"() {
        given: "uma tarefa em TODO que vence em 2030 e um alarme"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        tarefa.status = Tarefa.TarefaStatus.TODO
        configTarefas.adicionarTarefa(tarefa)
        def alarme = new Alarme(5)

        when: "adiciona o alarme"
        configTarefas.adicionarAlarme(tarefa, alarme)

        then: "ele fica na tarefa sem disparar"
        tarefa.alarmes == [alarme]
        !alarme.disparado
    }

    def "disparar alarme de tarefa em #status"() {
        given: "uma tarefa no status da tabela e um alarme"
        def tarefa = novaTarefa("Estudar", 3, "Estudos")
        tarefa.status = status
        def alarme = new Alarme(1)

        when: "dispara o alarme"
        configTarefas.dispararAlarme(tarefa, alarme)

        then: "só é marcado como disparado em TODO e DOING"
        alarme.disparado == disparado

        where:
        status                      | disparado
        Tarefa.TarefaStatus.BACKLOG | false
        Tarefa.TarefaStatus.TODO    | true
        Tarefa.TarefaStatus.DOING   | true
        Tarefa.TarefaStatus.DONE    | false
    }
}
