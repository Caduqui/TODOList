let criarCardForm = document.getElementById("criarCardForm");
let buttoncriarCard = document.getElementById("buttonCriarCard");

let listaTarefas = []
let edicao = null;

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
        status: tarefa.status || "TODO"
    }));
    renderizarTarefas();
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

    if(edicao !== null) {
        listaTarefas = listaTarefas.map(t => t.id === edicao ? { ...t, ...dadosTarefa} : t);
        edicao = null;
    } else {
        listaTarefas.push({
            id: Date.now(),
            ...dadosTarefa
        });
    }

    renderizarTarefas();
    criarCardForm.reset();
    document.querySelector(".criarCard").style.display = "none";
});
