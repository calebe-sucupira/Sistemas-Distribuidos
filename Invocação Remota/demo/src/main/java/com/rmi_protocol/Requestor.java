package com.rmi_protocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Requestor {
    private final String host;
    private final int port;

    public Requestor(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Mensagem doOperation(String objectReference, String methodId, byte[] arguments) throws Exception {
        try (
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            int requestId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            Mensagem requisicao = new Mensagem(0, requestId, objectReference, methodId, arguments);

            out.writeObject(requisicao);
            out.flush();
            
            System.out.println("LOG DO CLIENTE: Requisição (ID " + requestId + ") para o método '" + methodId + "' enviada.");
            
            Mensagem resposta = (Mensagem) in.readObject();
            
            System.out.println("LOG DO CLIENTE: Resposta (ID " + resposta.getRequestId() + ") recebida.");
            
            return resposta;
        }
    }
}