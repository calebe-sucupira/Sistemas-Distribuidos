package app;

import entidades.Celular;
import io.CelularOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TesteCelularOutputStream {

    public static void main(String[] args) {
        Celular celular1 = new Celular("C001", "Galaxy Super", "Samsung", 2500.00,
                                       "S25 Ultra", "Preto", "256GB", "12GB");
        Celular celular2 = new Celular("C002", "iPhone Max Pro", "Apple", 6500.00,
                                       "16 Pro Max", "Titânio Natural", "512GB", "8GB");
        Celular celular3 = new Celular("C003", "Pixel Avançado", "Google", 3200.00,
                                       "9 Pro", "Azul Celeste", "128GB", "8GB");

        Celular[] meusCelulares = {celular1, celular2, celular3};
        int numeroDeCelularesParaEnviar = meusCelulares.length;

        int[] bytesConfig = {10, 20, 4};

        // ---- Teste 2.b.i: Enviar para System.out (saída padrão/consola) ----
        System.out.println("--- INICIANDO TESTE COM SYSTEM.OUT ---");
        try (CelularOutputStream cosSysOut = new CelularOutputStream(meusCelulares, numeroDeCelularesParaEnviar, bytesConfig, System.out)) {
            System.out.println("Enviando dados dos celulares");
            cosSysOut.enviarTodosOsCelulares(); 
            System.out.flush(); 
            System.out.println("\n--- FIM DO TESTE COM SYSTEM.OUT ---");
        } catch (IOException e) {
            System.err.println("Erro ao testar com System.out: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n================================================\n");

        // ---- Teste 2.b.ii: Enviar para um arquivo (FileOutputStream) ----
        System.out.println("--- INICIANDO TESTE COM FILEOUTPUTSTREAM ---");
        String nomeArquivo = "celulares_output.dat"; 
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo);
             CelularOutputStream cosFile = new CelularOutputStream(meusCelulares, numeroDeCelularesParaEnviar, bytesConfig, fos)) {
            System.out.println("Enviando dados dos celulares para o arquivo: " + nomeArquivo);
            cosFile.enviarTodosOsCelulares(); 
            System.out.println("Dados dos celulares enviados para o arquivo '" + nomeArquivo + "' com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao testar com FileOutputStream: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("--- FIM DO TESTE COM FILEOUTPUTSTREAM ---");

        // ---- Teste 2.b.iii: Enviar para um Servidor TCP ----
        testarEnvioParaServidorTCP(meusCelulares, numeroDeCelularesParaEnviar, bytesConfig);
    }

    public static void testarEnvioParaServidorTCP(Celular[] celularesParaEnviar, int numCelulares, int[] configBytes) {
        System.out.println("\n================================================\n");
        System.out.println("--- INICIANDO TESTE COM SERVIDOR TCP ---");

        String hostServidor = "localhost"; 
        int portaServidor = 12345;     

        try (Socket socketParaServidor = new Socket(hostServidor, portaServidor);
             OutputStream outputStreamDoSocket = socketParaServidor.getOutputStream();
             CelularOutputStream cosTCP = new CelularOutputStream(celularesParaEnviar, numCelulares, configBytes, outputStreamDoSocket)) {

            System.out.println("Conectado ao servidor TCP em " + hostServidor + ":" + portaServidor);
            System.out.println("Enviando dados dos celulares para o servidor...");

            cosTCP.enviarTodosOsCelulares(); 

            System.out.println("Dados dos celulares enviados para o servidor com sucesso.");

        } catch (IOException e) {
            System.err.println("Erro no cliente TCP ao tentar enviar dados: " + e.getMessage());
        }
        System.out.println("--- FIM DO TESTE COM SERVIDOR TCP ---");
    }
}