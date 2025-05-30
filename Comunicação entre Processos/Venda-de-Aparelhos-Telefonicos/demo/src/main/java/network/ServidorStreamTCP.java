package network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorStreamTCP {

    public static void main(String[] args) {
        int porta = 12345;
        String arquivoParaEnviar = "celulares_output.dat";

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("ServidorStreamTCP (Modo Envio) iniciado na porta " + porta + ".");
            System.out.println("Aguardando conexão do cliente para enviar o arquivo: " + arquivoParaEnviar);

            Socket socketDoCliente = serverSocket.accept();
            System.out.println("Cliente conectado: " + socketDoCliente.getInetAddress().getHostAddress() + ":" + socketDoCliente.getPort());

            try (FileInputStream fis = new FileInputStream(arquivoParaEnviar);
                 OutputStream outputStreamDoCliente = socketDoCliente.getOutputStream()) {

                System.out.println("Enviando dados do arquivo '" + arquivoParaEnviar + "' para o cliente...");
                byte[] buffer = new byte[4096]; 
                int bytesLidosDoArquivo;
                long totalBytesEnviados = 0;

                while ((bytesLidosDoArquivo = fis.read(buffer)) != -1) {
                    outputStreamDoCliente.write(buffer, 0, bytesLidosDoArquivo);
                    totalBytesEnviados += bytesLidosDoArquivo;
                }
                outputStreamDoCliente.flush();

                System.out.println("Envio de dados concluído.");
                System.out.println("Total de " + totalBytesEnviados + " bytes enviados para o cliente.");

            } catch (IOException e) {
                System.err.println("Erro durante a leitura do arquivo ou comunicação com o cliente: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socketDoCliente.close(); 
                    System.out.println("Conexão com o cliente fechada.");
                } catch (IOException e) {
                    System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("ServidorStreamTCP (Modo Envio) encerrado.");
    }
}