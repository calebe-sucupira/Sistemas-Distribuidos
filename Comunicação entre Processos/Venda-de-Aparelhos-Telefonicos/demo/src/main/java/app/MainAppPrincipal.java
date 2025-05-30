package app;

import network.ServidorLojaTCP;

public class MainAppPrincipal {

    public static void main(String[] args) {
        System.out.println("###########################################################");
        System.out.println("###     SISTEMA DE VENDA DE APARELHOS TELEFÔNICOS     ###");
        System.out.println("###########################################################");
        System.out.println();

        int portaServidor = 54321;

        Thread threadDoServidor = new Thread(() -> {
            System.out.println("[APP PRINCIPAL] Iniciando o ServidorLojaTCP na porta " + portaServidor + " em uma nova thread...");
            ServidorLojaTCP servidor = new ServidorLojaTCP(portaServidor);
            servidor.iniciar(); 
        }, "Thread-ServidorLoja");

        threadDoServidor.start(); 

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("[APP PRINCIPAL] Thread principal interrompida enquanto esperava o servidor iniciar.");
            Thread.currentThread().interrupt(); 
        }

        System.out.println();
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println(">>> Para interagir com o sistema, execute a classe network.ClienteLojaTCP <<<");
        System.out.println("O cliente tentará se conectar a: localhost:" + portaServidor);
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println();
    }
}