const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { MudancaStatusEmMassa } = require("../../../main/javascript/front_end/MudancaStatusEmMassa.js");

function criarTarefa(id, status) {
    return {
        id,
        name: "Tarefa " + id,
        description: "Descrição " + id,
        date: "2026-10-12T17:00",
        priority: 3,
        category: "Trabalho",
        status,
        alarmes: []
    };
}

function statusDe(tarefas) {
    return tarefas.map(tarefa => tarefa.status);
}

describe("MudancaStatusEmMassa", () => {
    describe("aplicar", () => {
        it("given três tarefas em TODO selecionadas, when aplicar DONE, then as três ficam em DONE e devolve 3", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "TODO"), criarTarefa(2, "TODO"), criarTarefa(3, "TODO")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1, 2, 3], "DONE");

            // then
            assert.equal(alteradas, 3);
            assert.deepEqual(statusDe(tarefas), ["DONE", "DONE", "DONE"]);
        });

        it("given tarefas selecionadas em BACKLOG, TODO e DOING, when aplicar DONE, then todas ficam em DONE e devolve 3", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "BACKLOG"), criarTarefa(2, "TODO"), criarTarefa(3, "DOING")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1, 2, 3], "DONE");

            // then
            assert.equal(alteradas, 3);
            assert.deepEqual(statusDe(tarefas), ["DONE", "DONE", "DONE"]);
        });

        it("given uma em TODO e uma em DONE selecionadas, when aplicar DONE, then devolve 1", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "TODO"), criarTarefa(2, "DONE")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1, 2], "DONE");

            // then
            assert.equal(alteradas, 1);
            assert.deepEqual(statusDe(tarefas), ["DONE", "DONE"]);
        });

        it("given todas as selecionadas já em DONE, when aplicar DONE, then devolve 0", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "DONE"), criarTarefa(2, "DONE")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1, 2], "DONE");

            // then
            assert.equal(alteradas, 0);
            assert.deepEqual(statusDe(tarefas), ["DONE", "DONE"]);
        });

        it("given uma tarefa não selecionada, when aplicar, then o status dela não muda", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "TODO"), criarTarefa(2, "TODO")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1], "DOING");

            // then
            assert.equal(alteradas, 1);
            assert.deepEqual(statusDe(tarefas), ["DOING", "TODO"]);
        });

        it("given um id selecionado que não existe na lista, when aplicar, then ele é ignorado e as demais são tratadas", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefas = [criarTarefa(1, "BACKLOG"), criarTarefa(2, "BACKLOG")];

            // when
            const alteradas = mudanca.aplicar(tarefas, [1, 99, 2], "TODO");

            // then
            assert.equal(alteradas, 2);
            assert.deepEqual(statusDe(tarefas), ["TODO", "TODO"]);
            assert.equal(tarefas.length, 2);
        });

        it("given uma tarefa selecionada com alarmes, when aplicar, then só o status muda", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();
            const tarefa = {
                id: 1760000000001,
                name: "Entregar relatório",
                description: "Relatório semanal",
                date: "2026-10-12T17:00",
                priority: "4",
                category: "Trabalho",
                status: "TODO",
                alarmes: [
                    { id: 1760000000002, prioridade: 2, disparado: true },
                    { id: 1760000000003, prioridade: 5, disparado: false }
                ]
            };
            const esperada = { ...structuredClone(tarefa), status: "DOING" };

            // when
            const alteradas = mudanca.aplicar([tarefa], [1760000000001], "DOING");

            // then
            assert.equal(alteradas, 1);
            assert.deepEqual(tarefa, esperada);
        });
    });

    describe("resumo", () => {
        it("given quantidade 3 e destino DONE, when resumo, then devolve \"3 tarefas movidas para DONE\"", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();

            // when
            const texto = mudanca.resumo(3, "DONE");

            // then
            assert.equal(texto, "3 tarefas movidas para DONE");
        });

        it("given quantidade 1 e destino DONE, when resumo, then devolve \"1 tarefa movida para DONE\"", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();

            // when
            const texto = mudanca.resumo(1, "DONE");

            // then
            assert.equal(texto, "1 tarefa movida para DONE");
        });

        it("given quantidade 0 e destino DONE, when resumo, then devolve \"Nenhuma tarefa alterada: as selecionadas já estavam em DONE\"", () => {
            // given
            const mudanca = new MudancaStatusEmMassa();

            // when
            const texto = mudanca.resumo(0, "DONE");

            // then
            assert.equal(texto, "Nenhuma tarefa alterada: as selecionadas já estavam em DONE");
        });
    });
});
