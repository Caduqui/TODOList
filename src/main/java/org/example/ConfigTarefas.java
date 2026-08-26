package org.example;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author Guilherme Lima Conte
 */

public class ConfigTarefas {
    private final List<Tarefa> tarefas;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ConfigTarefas() {
        this.tarefas = new ArrayList<>();
    }

    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
        rebalance();
    }

    public void rebalance() {
        tarefas.sort((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));
    }

    public void inspecionarTarefa(Tarefa tarefa) {

        System.out.println(tarefa);
    }

    public boolean removerTarefa(int id) {

        return tarefas.removeIf(t -> t.getId() == id);
    }

    public boolean atualizarStatus(int id, Tarefa.TarefaStatus novoStatus) {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) {
                t.setStatus(novoStatus);
                return true;
            }
        }
        return false;
    }

    public List<Tarefa> getTodasTarefas() {
        return new ArrayList<>(tarefas);
    }

    // verificar depois stream
    public List<Tarefa> getTarefasCategoria(String category) {
        return tarefas.stream().filter(t -> t.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasPrioridade(int priority) {
        return tarefas.stream().filter(t -> t.getPriority() == priority).collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasStatus(Tarefa.TarefaStatus status) {
        return tarefas.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
    }

    public Tarefa buscarTarefaId(int id) {
        for (Tarefa tarefa : tarefas) {
            if (tarefa.getId() == id) {
                return tarefa;
            }
        }
        return null;
    }

    public void agendarAlarme(Tarefa tarefa, Alarme alarme) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime horarioAlarme = alarme.calcularHorario(tarefa);

        long delay = Duration.between(agora, horarioAlarme).toMillis();

        if (delay > 0) {
            scheduler.schedule( () -> dispararAlarme(tarefa, alarme), delay, TimeUnit.MILLISECONDS);
        } else if (agora.isBefore(tarefa.getDate())) {
            // já entrou no período de antecedencia
            dispararAlarme(tarefa, alarme);
        }
    }

    public void adicionarAlarme(Tarefa tarefa, Alarme alarme) {
        tarefa.adicionarAlarme(alarme);
        agendarAlarme(tarefa, alarme);
    }

    public void dispararAlarme(Tarefa tarefa, Alarme alarme) {
        if (tarefa.getStatus() == Tarefa.TarefaStatus.DONE) {
            return;
        }

        if (alarme.isDisparado()) {
            return;
        }

        System.out.println("ALARME!!!");
        System.out.println("Tarefa: " + tarefa.getName());
        System.out.println("Prioridade do alarme: " + alarme.getPrioridade());
        System.out.println("Vencimento: " + tarefa.getDate());
        System.out.println("Antecedência: " + alarme.getHorasAntecedencia() + "hr");
        alarme.setDisparado(true);
    }
}
