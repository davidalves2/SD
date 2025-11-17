package Servidor;

import Main.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Gestor {

    private final Map<String, String> utilizadores = new HashMap<>();
    private final Lock lockUtilizadores = new ReentrantLock();

    public Gestor() {
        utilizadores.put("admin", "admin");
        utilizadores.put("user", "user");
    }

    public boolean autenticar(String user, String pass) {
        lockUtilizadores.lock(); // Tranca o cofre de utilizadores
        try {
            System.out.println("[GESTOR] A tentar login: " + user);
            return utilizadores.containsKey(user) && utilizadores.get(user).equals(pass);
        } finally {
            lockUtilizadores.unlock();
        }
    }

    public boolean registar(String user, String pass) {
        lockUtilizadores.lock();
        try {
            System.out.println("[GESTOR] A tentar registo: " + user);

            if (!utilizadores.containsKey(user)) {
                utilizadores.put(user, pass);
                return true; // Sucesso
            }

            return false;
        } finally {
            lockUtilizadores.unlock(); // Destranca
        }
    }

    public void registarEvento(Event e) {
        // TODO: Implementar a lógica de guardar o evento no dia certo
        System.out.println("[GESTOR] Evento recebido: " + e.toString());
    }
}
