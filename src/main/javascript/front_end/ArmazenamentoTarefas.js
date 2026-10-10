class ArmazenamentoTarefas {
    constructor(storage, chave = "todolist.tarefas") {
        this.storage = storage;
        this.chave = chave;
    }

    salvar(tarefas) {
        try {
            this.storage.setItem(this.chave, JSON.stringify(tarefas));
            return true;
        } catch (erro) {
            return false;
        }
    }
}

if (typeof module !== "undefined") {
    module.exports = { ArmazenamentoTarefas };
}
