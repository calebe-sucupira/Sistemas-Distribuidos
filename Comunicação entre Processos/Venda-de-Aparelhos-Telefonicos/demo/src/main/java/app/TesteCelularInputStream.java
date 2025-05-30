package app;

import entidades.Celular;
import io.CelularInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class TesteCelularInputStream {

    public static void main(String[] args) {
        // ---- Teste 3.c: Ler de um arquivo (FileInputStream) ----
        testeLeituraDeArquivo();

        // ---- Teste 3.d: Ler de um Servidor TCP ----
        testarLeituraDeServidorTCP();
    }

    public static void testeLeituraDeArquivo() {
        String nomeArquivoEntrada = "celulares_output.dat";

        int[] bytesConfig = {10, 20, 4};

        System.out.println("--- INICIANDO TESTE DE LEITURA COM FileInputStream DO ARQUIVO: " + nomeArquivoEntrada + " ---");

        try (InputStream fis = new FileInputStream(nomeArquivoEntrada);
             CelularInputStream cis = new CelularInputStream(fis, bytesConfig)) { 
            List<Celular> celularesLidos = cis.readAllCelulares(); 

            if (celularesLidos.isEmpty()) {
                System.out.println("Nenhum celular foi lido do arquivo. O arquivo pode estar vazio ou o formato é inesperado.");
            } else {
                System.out.println("\n--- Celulares Lidos do Arquivo (" + nomeArquivoEntrada + ") ---");
                for (Celular celular : celularesLidos) {
                    System.out.println("---------------------------------");
                    System.out.println("Código Lido: " + celular.getCodigo());
                    System.out.println("Modelo Lido: " + celular.getModelo());
                    System.out.println("Memória Interna (reconstruída): " + celular.getMemoriaInterna());
                    System.out.println("Nome (placeholder): " + celular.getNome());
                    System.out.println("Marca (placeholder): " + celular.getMarca());
                    System.out.println("Preço (placeholder): " + celular.getPreco());
                    System.out.println("Cor (placeholder): " + celular.getColor());
                    System.out.println("RAM (placeholder): " + celular.getMemoriaRam());
                }
                System.out.println("---------------------------------");
                System.out.println(celularesLidos.size() + " celular(es) lido(s) com sucesso do arquivo.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo com CelularInputStream: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("\n--- FIM DO TESTE COM FileInputStream ---");
    }

    public static void testarLeituraDeServidorTCP() {
        System.out.println("\n================================================\n");
        System.out.println("--- INICIANDO TESTE DE LEITURA COM CLIENTE TCP ---");

        String hostServidor = "localhost";
        int portaServidor = 12345; 

        int[] bytesConfig = {10, 20, 4}; 

        try (Socket socketParaServidor = new Socket(hostServidor, portaServidor);
             InputStream inputStreamDoSocket = socketParaServidor.getInputStream();
             CelularInputStream cisTCP = new CelularInputStream(inputStreamDoSocket, bytesConfig)) { 

            System.out.println("Conectado ao servidor TCP em " + hostServidor + ":" + portaServidor);
            System.out.println("Lendo todos os celulares do servidor via TCP...");

            List<Celular> celularesLidos = cisTCP.readAllCelulares(); 

            if (celularesLidos.isEmpty()) {
                System.out.println("Nenhum celular foi lido do servidor. O servidor pode não ter enviado dados ou a conexão foi interrompida.");
            } else {
                System.out.println("\n--- Celulares Lidos do Servidor TCP ---");
                for (Celular celular : celularesLidos) {
                    System.out.println("---------------------------------");
                    System.out.println("Código Lido: " + celular.getCodigo());
                    System.out.println("Modelo Lido: " + celular.getModelo());
                    System.out.println("Memória Interna (reconstruída): " + celular.getMemoriaInterna());
                    System.out.println("Nome (placeholder): " + celular.getNome());
                    System.out.println("Marca (placeholder): " + celular.getMarca());
                    System.out.println("Preço (placeholder): " + celular.getPreco());
                    System.out.println("Cor (placeholder): " + celular.getColor());
                    System.out.println("RAM (placeholder): " + celular.getMemoriaRam());
                }
                System.out.println("---------------------------------");
                System.out.println(celularesLidos.size() + " celular(es) lido(s) do servidor com sucesso.");
            }

        } catch (IOException e) {
            System.err.println("Erro no cliente TCP ao tentar ler dados do servidor: " + e.getMessage());
        }
        System.out.println("\n--- FIM DO TESTE DE LEITURA COM CLIENTE TCP ---");
    }
}