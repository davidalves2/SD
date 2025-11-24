package Servidor;

import Main.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Gestor {

    private final Map<String, String> utilizadores = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public Gestor() {
        utilizadores.put("admin", "admin");
        utilizadores.put("user", "user");
    }

    public boolean autenticar(String user, String pass) {
        readLock.lock();
        try {
            System.out.println("[GESTOR] A tentar login: " + user);
            return utilizadores.containsKey(user) && utilizadores.get(user).equals(pass);
        } finally {
            readLock.unlock();
        }
    }

    public boolean registar(String user, String pass) {
        writeLock.lock();
        try {
            System.out.println("[GESTOR] A tentar registo: " + user);

            if (!utilizadores.containsKey(user)) {
                utilizadores.put(user, pass);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public void registarEvento(Event e) {
        System.out.println("[GESTOR] Evento recebido: " + e.toString());
    }
}
