class MudancaStatusEmMassa {
    aplicar(tarefas, ids, statusDestino) {
        let alteradas = 0;

        tarefas.forEach(tarefa => {
            if (!ids.includes(tarefa.id)) {
                return;
            }

            // tarefa que já está no destino não conta como alterada
            if (tarefa.status === statusDestino) {
                return;
            }

            tarefa.status = statusDestino;
            alteradas++;
        });

        return alteradas;
    }

    resumo(quantidade, statusDestino) {
        if (quantidade === 0) {
            return "Nenhuma tarefa alterada: as selecionadas já estavam em " + statusDestino;
        }

        if (quantidade === 1) {
            return "1 tarefa movida para " + statusDestino;
        }

        return quantidade + " tarefas movidas para " + statusDestino;
    }
}

if (typeof module !== "undefined") {
    module.exports = { MudancaStatusEmMassa };
}
