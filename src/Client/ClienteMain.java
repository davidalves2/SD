package Client;

import java.io.IOException;
import java.util.Scanner;

public class ClienteMain {

    private static BibliotecaCliente cliente = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("--- Cliente de Casas Barbosa ---");

        try {
            cliente = new BibliotecaCliente(SERVER_HOST, SERVER_PORT);
            System.out.println("Conexão estabelecida com sucesso!");

            menuPrincipal();

        } catch (IOException e) {
            System.err.println("ERRO: Não foi possível conectar ao servidor em " + SERVER_HOST + ":" + SERVER_PORT);
            System.err.println("Certifique-se de que o ServidorMain está em execução.");
        } finally {
            if (cliente != null) {
                try {
                    cliente.close();
                } catch (IOException e) {
                }
            }
            scanner.close();
        }
    }

    private static void menuPrincipal() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Registar Novo Utilizador");
            System.out.println("3. Registar Venda (Requer Login)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                if (scanner.hasNextInt()) {
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            handleLogin();
                            break;
                        case 2:
                            handleRegistar();
                            break;
                        case 3:
                            handleRegistarVenda();
                            break;
                        case 0:
                            System.out.println("A encerrar conexão...");
                            break;
                        default:
                            System.out.println("Opção inválida.");
                    }
                } else {
                    System.out.println("Entrada inválida. Por favor, digite um número.");
                    scanner.nextLine();
                }
            } catch (Exception e) {
                System.err.println("ERRO DE EXECUÇÃO: " + e.getMessage());
                opcao = 0;
            }
        }
    }

    private static void handleLogin() throws Exception {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        String resultado = cliente.login(user, pass);
        System.out.println(resultado);
    }

    private static void handleRegistar() throws Exception {
        System.out.print("Novo Username: ");
        String user = scanner.nextLine();
        System.out.print("Nova Password: ");
        String pass = scanner.nextLine();

        String resultado = cliente.registar(user, pass);
        System.out.println(resultado);
    }

    private static void handleRegistarVenda() throws Exception {
        System.out.print("Nome do Produto: ");
        String produto = scanner.nextLine();
        System.out.print("Quantidade: ");
        int quantidade = scanner.nextInt();
        System.out.print("Preço Unitário: ");
        double preco = scanner.nextDouble();
        scanner.nextLine(); // Consome a linha pendente

        String resultado = cliente.registarVenda(produto, quantidade, preco);
        System.out.println(resultado);
    }
}