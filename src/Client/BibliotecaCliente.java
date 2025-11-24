package Client;

import Main.Demultiplexer;
import Main.Event;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BibliotecaCliente implements AutoCloseable {
    private final Demultiplexer demultiplexer;
    private int nextTag = 1;
    private final Lock tagLock = new ReentrantLock();

    public BibliotecaCliente(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        this.demultiplexer = new Demultiplexer(socket);
    }

    private int getNextTag() {
        tagLock.lock();
        try {
            return nextTag++;
        } finally {
            tagLock.unlock();
        }
    }

    public void close() throws IOException {
        demultiplexer.close();
    }

    public String login(String user, String pass) throws Exception {
        int tag = getNextTag();
        byte[] responseData;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(1);
            out.writeUTF(user);
            out.writeUTF(pass);
            out.flush();

            demultiplexer.send(tag, baos.toByteArray());
            responseData = demultiplexer.receive(tag);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(responseData));
            int statusCode = in.readInt();
            String mensagem = in.readUTF();

            if (statusCode == 1) {
                return "SUCESSO: " + mensagem;
            } else {
                throw new Exception("FALHA no Login: " + mensagem);
            }
        } catch (InterruptedException | IOException e) {
            throw new Exception("Erro de comunicação ao fazer login.", e);
        }
    }

    public String registar(String user, String pass) throws Exception {
        int tag = getNextTag();
        byte[] responseData;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(2);
            out.writeUTF(user);
            out.writeUTF(pass);
            out.flush();

            demultiplexer.send(tag, baos.toByteArray());
            responseData = demultiplexer.receive(tag);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(responseData));
            int statusCode = in.readInt();
            String mensagem = in.readUTF();

            if (statusCode == 1) {
                return "SUCESSO: " + mensagem;
            } else {
                throw new Exception("FALHA no Registo: " + mensagem);
            }
        } catch (InterruptedException | IOException e) {
            throw new Exception("Erro de comunicação ao registar.", e);
        }
    }

    public String registarVenda(String produto, int quantidade, double preco) throws Exception {
        int tag = getNextTag();
        byte[] responseData;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(3);

            Event e = new Event(produto, quantidade, preco);
            e.serealize(out);
            out.flush();

            demultiplexer.send(tag, baos.toByteArray());
            responseData = demultiplexer.receive(tag);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(responseData));
            int statusCode = in.readInt();
            String mensagem = in.readUTF();

            if (statusCode == 1) {
                return "SUCESSO: " + mensagem;
            } else {
                throw new Exception("FALHA ao registar venda: " + mensagem);
            }
        } catch (InterruptedException | IOException e) {
            throw new Exception("Erro de comunicação ao registar venda.", e);
        }
    }
}
