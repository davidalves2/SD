package Main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private final Connector connector;
    private final Map<Integer, byte[]> buffer = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private IOException exception = null;

    public Demultiplexer(Socket socket) throws IOException {
        this.connector = new Connector(socket);
        new Thread(this::receiveLoop).start();
    }

    public void send(int tag, byte[] data) throws IOException {
        if (exception != null) throw exception;
        connector.send(tag, data);
    }

    public byte[] receive(int tag) throws InterruptedException, IOException {
        lock.lock();
        try {
            while (!buffer.containsKey(tag) && exception == null) {
                condition.await();
            }

            if (exception != null) {
                throw new IOException("A thread de leitura falhou", exception);
            }

            return buffer.remove(tag);
        } finally {
            lock.unlock();
        }
    }

    private void receiveLoop() {
        try {
            while (true) {
                Frame frame = connector.receive();

                lock.lock();
                try {
                    buffer.put(frame.tag, frame.data);
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            lock.lock();
            try {
                exception = e;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void close() throws IOException {
        connector.close();
    }
}

