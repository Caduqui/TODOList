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

    carregar() {
        try {
            const conteudo = this.storage.getItem(this.chave);

            if (conteudo === null) {
                return null;
            }

            const tarefas = JSON.parse(conteudo);

            if (!Array.isArray(tarefas)) {
                throw new Error("o conteúdo gravado não é uma lista de tarefas");
            }

            return tarefas;
        } catch (erro) {
            console.error("Erro ao ler tarefas do local storage: ", erro);
            return null;
        }
    }
}

if (typeof module !== "undefined") {
    module.exports = { ArmazenamentoTarefas };
}
