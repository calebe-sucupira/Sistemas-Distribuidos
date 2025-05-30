package io;

import entidades.Celular;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CelularInputStream extends InputStream {

    private InputStream origem;
    private int[] bytesPorAtributo;
    private boolean fimDoStreamDeOrigem = false;

    public CelularInputStream(InputStream origem, int[] bytesPorAtributo) {
        if (origem == null) {
            throw new NullPointerException("InputStream de origem não pode ser nulo.");
        }
        if (bytesPorAtributo == null || bytesPorAtributo.length != 3) {
            throw new IllegalArgumentException("Array 'bytesPorAtributo' deve ter exatamente 3 elementos.");
        }
        if (bytesPorAtributo[2] != 4 && bytesPorAtributo[2] != Integer.BYTES) {
             System.err.println("Aviso no CelularInputStream: bytesPorAtributo[2] (para int armazenamentoGB) idealmente deveria ser 4. Usando o valor fornecido: " + bytesPorAtributo[2]);
        }
        this.origem = origem;
        this.bytesPorAtributo = bytesPorAtributo;
    }

    private int lerBlocoCompleto(byte[] bufferFornecido, int tamanhoEsperado) throws IOException {
        if (fimDoStreamDeOrigem) {
            return -1;
        }
        if (tamanhoEsperado == 0) {
            return 0;
        }

        int totalBytesLidosNesteBloco = 0;
        int bytesLidosNestaChamada = 0;

        while (totalBytesLidosNesteBloco < tamanhoEsperado &&
               (bytesLidosNestaChamada = origem.read(bufferFornecido, totalBytesLidosNesteBloco, tamanhoEsperado - totalBytesLidosNesteBloco)) != -1) {
            totalBytesLidosNesteBloco += bytesLidosNestaChamada;
        }

        if (totalBytesLidosNesteBloco == 0 && bytesLidosNestaChamada == -1 && tamanhoEsperado > 0) {
            fimDoStreamDeOrigem = true;
            return -1;
        } else if (totalBytesLidosNesteBloco < tamanhoEsperado) {
            fimDoStreamDeOrigem = true;
            System.err.println("Aviso CIS: Fim do stream alcançado prematuramente ou stream truncado. Esperava " + tamanhoEsperado +
                               " bytes, mas leu apenas " + totalBytesLidosNesteBloco);
        }
        return totalBytesLidosNesteBloco;
    }

    @Override
    public int read() throws IOException {
        if (fimDoStreamDeOrigem) {
            return -1;
        }
        int byteLido = origem.read();
        if (byteLido == -1) {
            fimDoStreamDeOrigem = true;
        }
        return byteLido;
    }

    public Celular readCelular() throws IOException {
        String codigoLido;
        String modeloLido;
        int armazenamentoLido;

        byte[] codigoBytes = new byte[bytesPorAtributo[0]];
        int lidosCodigo = lerBlocoCompleto(codigoBytes, bytesPorAtributo[0]);
        if (lidosCodigo < bytesPorAtributo[0]) {
            return null;
        }
        codigoLido = new String(codigoBytes, StandardCharsets.UTF_8).trim();

        byte[] modeloBytes = new byte[bytesPorAtributo[1]];
        int lidosModelo = lerBlocoCompleto(modeloBytes, bytesPorAtributo[1]);
        if (lidosModelo < bytesPorAtributo[1]) {
            System.err.println("Aviso CIS: Stream truncado ao ler modelo para o código: " + codigoLido + ". Lidos: " + lidosModelo);
            return null;
        }
        modeloLido = new String(modeloBytes, StandardCharsets.UTF_8).trim();

        byte[] armazenamentoBytes = new byte[bytesPorAtributo[2]];
        int lidosArmazenamento = lerBlocoCompleto(armazenamentoBytes, bytesPorAtributo[2]);
        if (lidosArmazenamento < bytesPorAtributo[2]) {
            System.err.println("Aviso CIS: Stream truncado ao ler armazenamento para o código: " + codigoLido + ". Lidos: " + lidosArmazenamento);
            return null;
        }
        armazenamentoLido = bytesParaInt(armazenamentoBytes, bytesPorAtributo[2]);

        return new Celular(
                codigoLido,
                "NomeNaoInformado",
                "MarcaNaoInformada",
                0.0,
                modeloLido,
                "CorNaoInformada",
                armazenamentoLido + "GB",
                "RAMNaoInformada"
        );
    }

    private int bytesParaInt(byte[] bytes, int numBytesParaLer) {
        int valor = 0;
        int bytesDisponiveisParaConversao = Math.min(numBytesParaLer, bytes.length);
        bytesDisponiveisParaConversao = Math.min(bytesDisponiveisParaConversao, 4);

        if (bytesDisponiveisParaConversao >= 4) {
             valor = ((bytes[0] & 0xFF) << 24) |
                    ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8)  |
                    ((bytes[3] & 0xFF));
        } else if (bytesDisponiveisParaConversao == 3) {
            valor = ((bytes[0] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF));
        } else if (bytesDisponiveisParaConversao == 2) {
            valor = ((bytes[0] & 0xFF) << 8) | ((bytes[1] & 0xFF));
        } else if (bytesDisponiveisParaConversao == 1) {
            valor = (bytes[0] & 0xFF);
        } else if (bytesDisponiveisParaConversao == 0 && numBytesParaLer > 0) {
             System.err.println("Aviso CIS: Nenhum byte disponível para converter para int, embora " + numBytesParaLer + " fossem esperados. Retornando 0.");
            return 0;
        }
         else if (bytesDisponiveisParaConversao == 0 && numBytesParaLer == 0) {
            return 0;
        }
        else {
            System.err.println("Aviso CIS: numBytesParaLer para int é " + numBytesParaLer + " e bytes disponíveis são " + bytesDisponiveisParaConversao + ". Retornando 0.");
            return 0;
        }
        return valor;
    }

    public List<Celular> readAllCelulares() throws IOException {
        List<Celular> listaCelulares = new ArrayList<>();
        Celular celular;
        while ((celular = readCelular()) != null) {
            listaCelulares.add(celular);
        }
        return listaCelulares;
    }

    @Override
    public void close() throws IOException {
        origem.close();
        super.close();
    }
}
