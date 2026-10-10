const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { ArmazenamentoTarefas } = require("../../../main/javascript/front_end/ArmazenamentoTarefas.js");

const CHAVE = "todolist.tarefas";

function criarStorageFalso() {
    const dados = {};

    return {
        getItem(chave) {
            return chave in dados ? dados[chave] : null;
        },
        setItem(chave, valor) {
            dados[chave] = String(valor);
        }
    };
}

function criarStorageQueFalhaAoGravar() {
    return {
        getItem() {
            return null;
        },
        setItem() {
            throw new Error("storage cheio");
        }
    };
}

describe("ArmazenamentoTarefas", () => {
    describe("salvar", () => {
        it("given uma lista com tarefas e alarmes, when salvar, then grava o JSON na chave e devolve true", () => {
            // given
            const storage = criarStorageFalso();
            const armazenamento = new ArmazenamentoTarefas(storage);
            const tarefas = [
                {
                    id: 1,
                    name: "Comprar café",
                    description: "Estoque para a semana",
                    date: "2026-08-21T09:00",
                    priority: 1,
                    category: "Pessoal",
                    status: "BACKLOG",
                    alarmes: [{ id: 10, prioridade: 3, disparado: false }]
                },
                {
                    id: 2,
                    name: "Estilizar cards",
                    description: "Cores por status",
                    date: "2026-08-19T18:00",
                    priority: 3,
                    category: "Frontend",
                    status: "TODO",
                    alarmes: []
                }
            ];

            // when
            const salvou = armazenamento.salvar(tarefas);

            // then
            assert.equal(salvou, true);
            assert.deepEqual(JSON.parse(storage.getItem(CHAVE)), tarefas);
        });

        it("given uma lista vazia, when salvar, then grava um array vazio na chave", () => {
            // given
            const storage = criarStorageFalso();
            const armazenamento = new ArmazenamentoTarefas(storage);

            // when
            const salvou = armazenamento.salvar([]);

            // then
            assert.equal(salvou, true);
            assert.equal(storage.getItem(CHAVE), "[]");
        });

        it("given um storage que falha ao gravar, when salvar, then devolve false sem propagar o erro", () => {
            // given
            const armazenamento = new ArmazenamentoTarefas(criarStorageQueFalhaAoGravar());

            // when
            const salvou = armazenamento.salvar([{ id: 1, name: "Tarefa" }]);

            // then
            assert.equal(salvou, false);
        });
    });
});
