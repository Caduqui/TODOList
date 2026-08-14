package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Guilherme Lima Conte
 */

public class ConfigTarefas {
    private List<Tarefa> tarefas;

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

    public List<Tarefa> getTarefasCategoria(String category) {
        return tarefas.stream().filter(t -> t.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasPrioridade(int priority) {
        return tarefas.stream().filter(t -> t.getPriority() == priority).collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasStatus(Tarefa.TarefaStatus status) {
        return tarefas.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
    }
}
