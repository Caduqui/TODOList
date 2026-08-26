package org.example;

import java.time.LocalDateTime;

/**
 *
 * @author Guilherme Lima Conte
 */

public class Alarme {
    private int prioridade;
    private boolean disparado;

    public Alarme(int prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new IllegalArgumentException(
                    "Prioridade do alarme deve ser entre 1 e 5"
            );
        }

        this.prioridade = prioridade;
        this.disparado = false;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public boolean isDisparado() {
        return disparado;
    }

    public void setDisparado(boolean disparado) {
        this.disparado = disparado;
    }

    public long getHorasAntecedencia() {
        return switch (prioridade) {
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 6;
            case 4 -> 12;
            case 5 -> 24;
            default -> throw new IllegalStateException(
                    "Prioridade inválida"
            );
        };
    }

    public LocalDateTime calcularHorario(Tarefa tarefa) {
        return tarefa.getDate().minusHours(getHorasAntecedencia());
    }
}
