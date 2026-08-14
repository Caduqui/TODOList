package org.example;
import java.time.LocalDate;

public class Tarefa {
    public enum TarefaStatus {
        TODO, DOING, DONE
    }

    private static int contadorId = 1;
    private int id;
    private String name;
    private String description;
    private LocalDate date;
    private byte priority;
    private String category;
    private TarefaStatus status;

    public Tarefa (String name, String description, LocalDate date, byte priority, String category) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Prioridade deve ser entre 1 e 5");
        }
        this.id = contadorId++;
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.category = category;
        this.status = TarefaStatus.TODO;
    }

    public int getId() {
        return id;
    }

    public String getName()  {
        return name;
    }

    public String getDescription()  {
        return description;
    }

    public LocalDate getDate()  {
        return date;
    }
    public byte getPriority()  {
        return priority;
    }
    public String getCategory()  {
        return category;
    }
    public TarefaStatus getStatus()  {
        return status;
    }

    public void setStatus(TarefaStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Id: %d\n Nome: %s\n Descrição: %s\n data: %s\n prioridade: %d\n categoria: %s\n Status: %s",
                id, name, description, date, priority, category, status);
    }
}


