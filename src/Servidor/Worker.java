package Servidor;

import Main.Connector;

public class Worker implements Runnable {
    private final Connector connector;

    public Worker(Connector connector) {
        this.connector = connector;
    }

    public void run() {
        try {
            while (true) {

                System.out.println("Worker a correr (Lógica pendente do ajuste ReceiveAny)");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectou.");
        }
    }


}
