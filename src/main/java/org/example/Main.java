package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;

/**
 *
 * @author Guilherme Lima Conte
 */

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ConfigTarefas configTarefas = new ConfigTarefas();
    private static final DateTimeFormatter dataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1- Adicionar tarefa");
            System.out.println("2- Inspecionar tarefas");
            System.out.println("3- Remover tarefa");
            System.out.println("4- Filtrar por categoria");
            System.out.println("5- Filtrar por prioridade");
            System.out.println("6- Filtrar por status");
            System.out.println("7- Atualizar status de uma tarefa");
            System.out.println("8- Adicionar alarme");
            System.out.println("9- Sair");

            try {
                byte opcao = Byte.parseByte(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        adicionarTarefa();
                        break;
                    case 2:
                        inspecionarTarefa();
                        break;
                    case 3:
                        removerTarefa();
                        break;
                    case 4:
                        filtroCategoria();
                        break;
                    case 5:
                        filtroPrioridade();
                        break;
                    case 6:
                        filtroStatus();
                        break;
                    case 7:
                        atualizarStatus();
                        break;
                    case 8:
                        adicionarAlarme();
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

        }
    }

    private static void adicionarTarefa() {
        System.out.print("Nome: ");
        String name = scanner.nextLine();

        System.out.print("Descrição: ");
        String description = scanner.nextLine();

        System.out.print("Data de vencimento (dd/mm/aaaa) HH:mm: ");
        LocalDateTime date = LocalDateTime.parse(scanner.nextLine(), dataHora);

        System.out.print("Prioridade (1 a 5): ");
        byte priority = Byte.parseByte(scanner.nextLine());

        System.out.print("Categoria: ");
        String category = scanner.nextLine();

        Tarefa novaTarefa = new Tarefa(name, description, date, priority, category);
        configTarefas.adicionarTarefa(novaTarefa);
        System.out.println("Tarefa adicionada!");
    }

    private static void inspecionarTarefa() {
        printTarefa(configTarefas.getTodasTarefas());
    }

    private static void filtroCategoria() {
        System.out.print("Digite a categoria: ");
        String lerCategoria = scanner.nextLine();
        printTarefa(configTarefas.getTarefasCategoria(lerCategoria));
    }

    private static void filtroPrioridade() {
        System.out.print("Digite a prioridade de 1 a 5:");
        byte escolhaPrioridade = Byte.parseByte(scanner.nextLine());
        if (escolhaPrioridade >= 1 && escolhaPrioridade <= 5) {
            printTarefa(configTarefas.getTarefasPrioridade(escolhaPrioridade));
        } else {
            System.out.println("Opção inválida!");
        }
    }

    private static void filtroStatus() {
        System.out.print("Digite o status (TODO, DOING, DONE): ");
        Tarefa.TarefaStatus status = Tarefa.TarefaStatus.valueOf(scanner.nextLine().toUpperCase());
        printTarefa(configTarefas.getTarefasStatus(status));
    }

    private static void atualizarStatus() {
        System.out.print("ID da tarefa:");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Novo status (TODO, DOING, DONE): ");
        Tarefa.TarefaStatus status = Tarefa.TarefaStatus.valueOf(scanner.nextLine().toUpperCase());

        if (configTarefas.atualizarStatus(id, status)) {
            System.out.println("Status atualizado!");
        } else {
            System.out.println("Tarefa não encontrada");
        }
    }

    private static void removerTarefa() {
        System.out.print("Insira o id da tarefa a ser removida: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (configTarefas.removerTarefa(id)) {
            System.out.println("Tarefa removida!");
        } else {
            System.out.println("Tarefa não encontrada");
        }
    }

    private static void printTarefa(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada");
        } else {
            System.out.println("Lista de tarefas:");
            tarefas.forEach(System.out::println);
        }
    }

    private static void adicionarAlarme() {
        System.out.println("Insira o id da tarefa que você deseja adicionar o alarme: ");
        int id = Integer.parseInt(scanner.nextLine());
        Tarefa tarefaSelecionada = configTarefas.buscarTarefaId(id);

        if (tarefaSelecionada == null) {
        System.out.println("tarefa não existe");
        return;
        }

        System.out.println("Digite a prioridade do alarme (1 a 5): ");

        int prioridade = Integer.parseInt(scanner.nextLine());
        Alarme alarme = new Alarme(prioridade);

        configTarefas.adicionarAlarme(tarefaSelecionada, alarme);

        System.out.println("Alarme adicionado");
    }
}
