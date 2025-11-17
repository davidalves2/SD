package Servidor;

import Main.Connector;

import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMain {
    public static void main(String[] args) {

        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Servidor criado com sucesso!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente encontrado!");

                Connector connector = new Connector(clientSocket);

                Worker worker = new Worker(connector);

                Thread threadClient = new Thread(worker);
                threadClient.start();

            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
