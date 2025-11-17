package Servidor;

import Main.Connector;
import Main.Event;
import Main.Frame;

public class Worker implements Runnable {
    private final Connector connector;
    private final Gestor gestor;

    private boolean autenticado = false;
    private String username = null;

    public Worker(Connector connector, Gestor gestor) {
        this.connector = connector;
        this.gestor = gestor;
    }

    public void run() {
        try (connector){
            while (true) {


            }
        } catch (Exception e) {
            System.out.println("Cliente desconectou.");
        }
    }


}
