class SelecaoTarefas {
    constructor() {
        this.selecionadas = new Set();
    }

    alternar(id, marcada) {
        if (marcada) {
            this.selecionadas.add(id);
        } else {
            this.selecionadas.delete(id);
        }
    }

    estaSelecionada(id) {
        return this.selecionadas.has(id);
    }

    remover(id) {
        this.selecionadas.delete(id);
    }

    limpar() {
        this.selecionadas.clear();
    }

    quantidade() {
        return this.selecionadas.size;
    }

    ids() {
        return [...this.selecionadas];
    }

    textoContador() {
        const quantidade = this.quantidade();

        if (quantidade === 1) {
            return "1 selecionada";
        }

        return quantidade + " selecionadas";
    }
}

if (typeof module !== "undefined") {
    module.exports = { SelecaoTarefas };
}
