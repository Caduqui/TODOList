const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { SelecaoTarefas } = require("../../../main/javascript/front_end/SelecaoTarefas.js");

function criarSelecaoCom(...ids) {
    const selecao = new SelecaoTarefas();

    ids.forEach(id => selecao.alternar(id, true));

    return selecao;
}

describe("SelecaoTarefas", () => {
    describe("alternar", () => {
        it("given seleção vazia, when alternar um id como marcado, then a quantidade é 1 e o id está selecionado", () => {
            // given
            const selecao = new SelecaoTarefas();

            // when
            selecao.alternar(7, true);

            // then
            assert.equal(selecao.quantidade(), 1);
            assert.equal(selecao.estaSelecionada(7), true);
        });

        it("given um id selecionado, when alternar o mesmo id como desmarcado, then a quantidade é 0", () => {
            // given
            const selecao = criarSelecaoCom(7);

            // when
            selecao.alternar(7, false);

            // then
            assert.equal(selecao.quantidade(), 0);
            assert.equal(selecao.estaSelecionada(7), false);
        });

        it("given um id selecionado, when alternar o mesmo id como marcado de novo, then a quantidade continua 1", () => {
            // given
            const selecao = criarSelecaoCom(7);

            // when
            selecao.alternar(7, true);

            // then
            assert.equal(selecao.quantidade(), 1);
        });
    });

    describe("remover", () => {
        it("given dois ids selecionados, when remover um deles, then a quantidade é 1 e o outro continua selecionado", () => {
            // given
            const selecao = criarSelecaoCom(7, 8);

            // when
            selecao.remover(7);

            // then
            assert.equal(selecao.quantidade(), 1);
            assert.equal(selecao.estaSelecionada(7), false);
            assert.equal(selecao.estaSelecionada(8), true);
        });

        it("given um id que não está selecionado, when remover, then nada muda", () => {
            // given
            const selecao = criarSelecaoCom(7, 8);

            // when
            selecao.remover(99);

            // then
            assert.equal(selecao.quantidade(), 2);
            assert.equal(selecao.estaSelecionada(7), true);
            assert.equal(selecao.estaSelecionada(8), true);
        });
    });

    describe("limpar", () => {
        it("given dois ids selecionados, when limpar, then a quantidade é 0", () => {
            // given
            const selecao = criarSelecaoCom(7, 8);

            // when
            selecao.limpar();

            // then
            assert.equal(selecao.quantidade(), 0);
            assert.equal(selecao.estaSelecionada(7), false);
            assert.equal(selecao.estaSelecionada(8), false);
        });
    });

    describe("ids", () => {
        it("given três ids selecionados, when ids, then devolve os três", () => {
            // given
            const selecao = criarSelecaoCom(7, 1760000000001, 8);

            // when
            const ids = selecao.ids();

            // then
            assert.deepEqual([...ids].sort((a, b) => a - b), [7, 8, 1760000000001]);
        });
    });

    describe("textoContador", () => {
        it("given três selecionados, when textoContador, then devolve \"3 selecionadas\"", () => {
            // given
            const selecao = criarSelecaoCom(7, 8, 9);

            // when
            const texto = selecao.textoContador();

            // then
            assert.equal(texto, "3 selecionadas");
        });

        it("given um selecionado, when textoContador, then devolve \"1 selecionada\"", () => {
            // given
            const selecao = criarSelecaoCom(7);

            // when
            const texto = selecao.textoContador();

            // then
            assert.equal(texto, "1 selecionada");
        });

        it("given nenhum selecionado, when textoContador, then devolve \"0 selecionadas\"", () => {
            // given
            const selecao = new SelecaoTarefas();

            // when
            const texto = selecao.textoContador();

            // then
            assert.equal(texto, "0 selecionadas");
        });
    });
});
