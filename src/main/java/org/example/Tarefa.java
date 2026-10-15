package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Guilherme Lima Conte
 */

public class Tarefa {
    public enum TarefaStatus {
        BACKLOG, TODO, DOING, DONE
    }

    private static int contadorId = 1;
    private int id;
    private String name;
    private String description;
    private LocalDateTime date;
    private byte priority;
    private String category;
    private TarefaStatus status;
    private List<Alarme> alarmes;

    public Tarefa(String name, String description, LocalDateTime date, byte priority, String category) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Prioridade deve ser entre 1 e 5");
        }
        this.id = contadorId++;
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.category = category;
        this.status = TarefaStatus.BACKLOG;
        this.alarmes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Prioridade deve ser entre 1 e 5");
        }
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TarefaStatus getStatus() {
        return status;
    }

    public void setStatus(TarefaStatus status) {
        this.status = status;
    }

    public void adicionarAlarme(Alarme alarme) {
        alarmes.add(alarme);
    }

    public List<Alarme> getAlarmes() {
        return new ArrayList<>(alarmes);
    }

    @Override
    public String toString() {
        return String.format("Id: %d\n Nome: %s\n Descrição: %s\n data: %s\n prioridade: %d\n categoria: %s\n Status: %s",
                id, name, description, date, priority, category, status);
    }
}


