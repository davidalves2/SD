package Servidor;

import Main.Connector;
import Main.Event;
import Main.Frame;

import java.io.*;

public class Worker implements Runnable {

    // As ferramentas do Worker
    private final Connector connector;
    private final Gestor gestor;

    private boolean autenticado = false;
    private String username = null;


    public Worker(Connector connector, Gestor gestor) {
        this.connector = connector;
        this.gestor = gestor;
    }

    public void run() {

        try (connector) {

            while (true) {
                Frame request = connector.receive();

                Frame response = processarRequest(request);

                connector.send(response.tag, response.data);
            }

        } catch (EOFException e) {
            System.out.println("Cliente " + (username != null ? username : "") + " desconectou-se.");
        } catch (IOException e) {
            System.out.println("Erro de comunicação com o cliente: " + e.getMessage());
        }
    }

    private Frame processarRequest(Frame request) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(request.data);
            DataInputStream in = new DataInputStream(bais);

            int opCode = in.readInt();

            if (!autenticado && opCode > 2) {
                return criarRespostaErro(request.tag, "Cliente não autenticado.");
            }

            switch (opCode) {
                case 1: // Login
                    return handleLogin(in, request.tag);
                case 2: // Registo
                    return handleRegisto(in, request.tag);
                case 3: // Registar Evento (Venda)
                    return handleRegistarEvento(in, request.tag);


                default:
                    return criarRespostaErro(request.tag, "Operação desconhecida.");
            }

        } catch (IOException e) {
            return criarRespostaErro(request.tag, "Erro a ler o pedido: " + e.getMessage());
        }
    }

    private Frame handleLogin(DataInputStream in, int tag) throws IOException {
        String user = in.readUTF(); // Lê o username (texto)
        String pass = in.readUTF(); // Lê a password (texto)

        if (gestor.autenticar(user, pass)) {
            this.autenticado = true;
            this.username = user;
            return criarRespostaSucesso(tag, "Login OK");
        } else {
            return criarRespostaErro(tag, "Credenciais inválidas");
        }
    }

    private Frame handleRegisto(DataInputStream in, int tag) throws IOException {
        String user = in.readUTF();
        String pass = in.readUTF();

        if (gestor.registar(user, pass)) {
            return criarRespostaSucesso(tag, "Registo OK");
        } else {
            return criarRespostaErro(tag, "Utilizador já existe");
        }
    }

    private Frame handleRegistarEvento(DataInputStream in, int tag) throws IOException {
        Event e = Event.deserialize(in);

        gestor.registarEvento(e);

        return criarRespostaSucesso(tag, "Evento registado");
    }

    private Frame criarRespostaErro(int tag, String mensagem) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);

            out.writeInt(0);
            out.writeUTF(mensagem);
            out.flush();

            return new Frame(tag, baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    private Frame criarRespostaSucesso(int tag, String mensagem) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);

            out.writeInt(1);
            out.writeUTF(mensagem);
            out.flush();

            return new Frame(tag, baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
