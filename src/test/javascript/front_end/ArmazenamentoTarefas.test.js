const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { ArmazenamentoTarefas } = require("../../../main/javascript/front_end/ArmazenamentoTarefas.js");

const CHAVE = "todolist.tarefas";

function criarStorageFalso(dadosIniciais = {}) {
    const dados = { ...dadosIniciais };

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

function criarStorageQueFalhaAoLer() {
    return {
        getItem() {
            throw new Error("storage indisponível");
        },
        setItem() {
        }
    };
}

function criarTarefasComAlarme() {
    return [
        {
            id: 1760000000001,
            name: "Entregar relatório",
            description: "Relatório semanal",
            date: "2026-10-12T17:00",
            priority: "4",
            category: "Trabalho",
            status: "DOING",
            alarmes: [
                { id: 1760000000002, prioridade: 2, disparado: true },
                { id: 1760000000003, prioridade: 5, disparado: false }
            ]
        },
        {
            id: 5,
            name: "Comprar café",
            description: "Estoque para a semana",
            date: "2026-08-21",
            priority: 1,
            category: "Pessoal",
            status: "BACKLOG",
            alarmes: []
        }
    ];
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

    describe("carregar", () => {
        it("given chave ausente, when carregar, then devolve null", (t) => {
            // given
            t.mock.method(console, "error", () => {});
            const armazenamento = new ArmazenamentoTarefas(criarStorageFalso());

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.equal(tarefas, null);
            assert.equal(console.error.mock.callCount(), 0);
        });

        it("given um array válido gravado, when carregar, then devolve as tarefas com id, alarmes e disparado preservados", () => {
            // given
            const gravadas = criarTarefasComAlarme();
            const storage = criarStorageFalso({ [CHAVE]: JSON.stringify(gravadas) });
            const armazenamento = new ArmazenamentoTarefas(storage);

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.deepEqual(tarefas, gravadas);
            assert.equal(tarefas[0].id, 1760000000001);
            assert.equal(tarefas[0].alarmes[0].id, 1760000000002);
            assert.equal(tarefas[0].alarmes[0].disparado, true);
            assert.equal(tarefas[0].alarmes[1].disparado, false);
        });

        it("given um array vazio gravado, when carregar, then devolve um array vazio e não null", () => {
            // given
            const storage = criarStorageFalso({ [CHAVE]: "[]" });
            const armazenamento = new ArmazenamentoTarefas(storage);

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.deepEqual(tarefas, []);
        });

        it("given um texto que não é JSON, when carregar, then devolve null", (t) => {
            // given
            t.mock.method(console, "error", () => {});
            const storage = criarStorageFalso({ [CHAVE]: "abc" });
            const armazenamento = new ArmazenamentoTarefas(storage);

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.equal(tarefas, null);
            assert.equal(console.error.mock.callCount(), 1);
        });

        it("given um JSON válido que não é array, when carregar, then devolve null", (t) => {
            // given
            t.mock.method(console, "error", () => {});
            const storage = criarStorageFalso({ [CHAVE]: JSON.stringify({ id: 1, name: "Tarefa" }) });
            const armazenamento = new ArmazenamentoTarefas(storage);

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.equal(tarefas, null);
            assert.equal(console.error.mock.callCount(), 1);
        });

        it("given um storage que falha ao ler, when carregar, then devolve null", (t) => {
            // given
            t.mock.method(console, "error", () => {});
            const armazenamento = new ArmazenamentoTarefas(criarStorageQueFalhaAoLer());

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.equal(tarefas, null);
            assert.equal(console.error.mock.callCount(), 1);
        });

        it("given uma lista salva com salvar, when carregar, then devolve uma lista igual", () => {
            // given
            const armazenamento = new ArmazenamentoTarefas(criarStorageFalso());
            const salvas = criarTarefasComAlarme();
            armazenamento.salvar(salvas);

            // when
            const tarefas = armazenamento.carregar();

            // then
            assert.deepEqual(tarefas, salvas);
        });
    });
});
