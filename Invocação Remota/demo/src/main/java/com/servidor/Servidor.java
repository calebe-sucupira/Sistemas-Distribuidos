package com.servidor;

import com.interfaces.ILojaService;
import com.servicos.LojaServiceImpl;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        int port = 6789;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor RMI iniciado na porta " + port);

            ILojaService lojaService = new LojaServiceImpl();

            while (true) {
                System.out.println("Aguardando conexão de cliente...");
                
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, lojaService);
                new Thread(clientHandler).start();
            }

        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

