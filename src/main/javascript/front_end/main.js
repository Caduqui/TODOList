const criarCardForm = document.getElementById("criarCardForm");
const buttoncriarCard = document.getElementById("buttonCriarCard");
const alarmeForm = document.getElementById("alarmeForm");
const alarmeContainer = document.getElementById("alarme_container")

let listaTarefas = []
let edicao = null;
let tarefaAlarmeSelecionado = null;

function renderizarTarefas() {
    document.querySelectorAll(".card_itens").forEach(card => card.remove());
    listaTarefas.sort((a, b) => Number(b.priority) - Number(a.priority));

    listaTarefas.forEach(tarefa => {
        let coluna = document.getElementById(tarefa.status);

        if (coluna) {
            let ul = document.createElement("ul");
            ul.classList.add("card_itens");

            ul.innerHTML = `
                <li><strong>Nome</strong>: ${tarefa.name} </li>
                <li><strong>Descrição:</strong> ${tarefa.description} </li>
                <li><strong>Prioridade:</strong> ${tarefa.priority} </li>
                <li><strong>Categoria:</strong> ${tarefa.category} </li>
                <li class="card_acoes">
                    <button type="button" onclick="prepararEdicao(${tarefa.id})">
                        <img src="assets/editar.png" alt="Editar"/>
                    </button>
                    <button type="button" onclick="removerTarefa(${tarefa.id})">
                        <img src="assets/lixeira.png" alt="Remover"/>
                    </button>
                    <button type="button" onclick="prepararAlarme(${tarefa.id})">
                        <img src="assets/alarme.png" alt="alarme"/>
                    </button>
                </li>
                <li class="tarefa_data">${tarefa.date} </li>
            `;
            coluna.appendChild(ul);
        }
    });
}

fetch("tarefas.json").then(response => response.json()).then(tarefas => {
    listaTarefas = tarefas.map((tarefa, index) => ({
        id: tarefa.id || Date.now() + index,
        name: tarefa.name,
        description: tarefa.description,
        date: tarefa.date,
        priority: tarefa.priority,
        category: tarefa.category,
        status: tarefa.status || "TODO",
        alarmes: tarefa.alarmes || []
    }));

    renderizarTarefas();
    verificarAlarmes();

}).catch(erro => console.error("Erro ao carregar json: ", erro));

window.removerTarefa = function (id) {
    listaTarefas = listaTarefas.filter(tarefa => tarefa.id !== id);
    renderizarTarefas();
};

window.prepararEdicao = function (id) {
    const tarefa = listaTarefas.find(t => t.id === id);
    if (!tarefa)
        return;

    edicao = id;
    document.getElementById("input_nome").value = tarefa.name;
    document.getElementById("input_descricao").value = tarefa.description;
    document.getElementById("input_data").value = tarefa.date;
    document.getElementById("input_prioridade").value = tarefa.priority;
    document.getElementById("input_categoria").value = tarefa.category;
    document.getElementById("input_status").value = tarefa.status;
    document.querySelector(".criarCard").style.display = "block";
};

window.prepararAlarme = function (id) {
    const tarefa = listaTarefas.find(t => t.id === id);
    if (!tarefa)
        return;

    tarefaAlarmeSelecionado = tarefa

    document.getElementById("alarme_nome_tarefa").textContent = "Tarefa: " + tarefa.name;
    renderizarAlarmes(tarefa);
    document.getElementById("alarme_container").style.display = "flex";
}

window.removerAlarme = function (idAlarme) {
    if (!tarefaAlarmeSelecionado)
        return

    tarefaAlarmeSelecionado.alarmes = tarefaAlarmeSelecionado.alarmes.filter(alarme => alarme.id !== idAlarme);

    renderizarAlarmes(tarefaAlarmeSelecionado);
}

alarmeForm.addEventListener("submit", (e) => {
    e.preventDefault();

    if (!tarefaAlarmeSelecionado)
        return

    const prioridade = Number(document.getElementById("alarme_prioridade").value);

    const alarme = {
        id: Date.now(),
        prioridade: prioridade,
        disparado: false
    };

    if (!tarefaAlarmeSelecionado.alarmes) {
        tarefaAlarmeSelecionado.alarmes = [];
    }

    tarefaAlarmeSelecionado.alarmes.push(alarme);
    renderizarAlarmes(tarefaAlarmeSelecionado);
    verificarAlarmes();
});

document.getElementById("cancelarAlarme").addEventListener("click", () => {
    document.getElementById("alarme_container").style.display = "none";
    tarefaAlarmeSelecionado = null;
});

function getHorasAntecedencia(prioridade) {
    switch (Number(prioridade)) {
        case 1:
            return 2;
        case 2:
            return 4;
        case 3:
            return 6;
        case 4:
            return 12;
        case 5:
            return 24;
        default:
            throw new Error("Prioridade inválida");

    }
}

function calcularHorarioAlarme(tarefa, alarme) {
    const vencimento = new Date(tarefa.date);
    const horas = getHorasAntecedencia(alarme.prioridade);
    return new Date(vencimento.getTime() - horas * 60 * 60 * 1000);
}

function verificarAlarmes() {
    const agora = new Date();

    listaTarefas.forEach(tarefa => {

        if (tarefa.status === "DONE")
            return

        if (!tarefa.alarmes)
            return

        const vencimento = new Date(tarefa.date)

        tarefa.alarmes.forEach(alarme => {
            if (alarme.disparado)
                return

            const horarioAlarme = calcularHorarioAlarme(tarefa, alarme);

            if (agora >= horarioAlarme && agora < vencimento) {
                dispararAlarme(tarefa, alarme);
            }
        });
    });
}

function dispararAlarme(tarefa, alarme) {
    if (alarme.disparado)
        return

    alarme.disparado = true;

    const aviso = document.createElement("div");

    aviso.classList.add("aviso_alarme");

    aviso.innerHTML = `
        <h3> ALARME!</h3>
        
        <p>A tarefa <strong>${tarefa.name}</strong> está proxíma do vencimento.</p>
        <p>Prioridade do alarme: ${alarme.prioridade}</p>
        <p>Antecedência: ${getHorasAntecedencia(alarme.prioridade)} horas</p>
        <p>Vencimento: ${new Date(tarefa.date).toLocaleString("pt-BR")}</p>
        <button type="button">Fechar</button>
    `;
    aviso.querySelector("button").addEventListener("click", () => {
        aviso.remove();
    });

    document.body.appendChild(aviso);
}

function renderizarAlarmes(tarefa) {
    const listaAlarmes = document.getElementById("lista_alarmes");

    listaAlarmes.innerHTML = "";

    if (!tarefa.alarmes || tarefa.alarmes.length === 0) {
        listaAlarmes.innerHTML = "<p>Nenhum alarme cadastrado.</p>";
        return;
    }

    tarefa.alarmes.forEach(alarme => {
        const div = document.createElement("div");

        div.classList.add("alarme_item");

        div.innerHTML = `
            <span>
                Prioridade ${alarme.prioridade}
                - ${getHorasAntecedencia(alarme.prioridade)}h antes
            </span>

            <button
                type="button"
                onclick="removerAlarme(${alarme.id})"
            >
                Excluir
            </button>
        `;

        listaAlarmes.appendChild(div);
    });
}


buttoncriarCard.addEventListener("click", () => {
    let divcriarCard = document.querySelector(".criarCard");

    edicao = null;
    criarCardForm.reset();
    document.getElementById("input_status").value = "TODO";

    if (divcriarCard.style.display == "none" || divcriarCard.style.display == "") {
        divcriarCard.style.display = "block";
    } else {
        divcriarCard.style.display = "none";
    }
});

criarCardForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const dadosTarefa = {
        name: document.getElementById("input_nome").value,
        description: document.getElementById("input_descricao").value,
        date: document.getElementById("input_data").value,
        priority: document.getElementById("input_prioridade").value,
        category: document.getElementById("input_categoria").value,
        status: document.getElementById("input_status").value
    };

    if (edicao !== null) {
        listaTarefas = listaTarefas.map(t => t.id === edicao ? {...t, ...dadosTarefa} : t);
        edicao = null;
    } else {
        listaTarefas.push({
            id: Date.now(),
            ...dadosTarefa,
            alarmes: []
        });
    }

    renderizarTarefas();
    criarCardForm.reset();
    document.querySelector(".criarCard").style.display = "none";
});

setInterval(verificarAlarmes, 60 * 1000)
