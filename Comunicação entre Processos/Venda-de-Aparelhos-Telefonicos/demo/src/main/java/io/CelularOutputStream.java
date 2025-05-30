package io;

import entidades.Celular;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CelularOutputStream extends OutputStream {

    private Celular[] celulares;
    private int numeroDeCelularesAEnviar;
    private int[] bytesPorAtributo;
    private OutputStream destino;
    private int celularAtualIndex = 0;
    private int atributoAtualIndex = -1;
    private int byteAtualNoAtributo = 0;
    private byte[] bufferAtributoAtual;

    public CelularOutputStream(Celular[] celulares, int numeroDeCelularesAEnviar, int[] bytesPorAtributo, OutputStream destino) {
        if (celulares == null || destino == null) {
            throw new NullPointerException("Array de celulares e destino não podem ser nulos.");
        }
        if (numeroDeCelularesAEnviar < 0 || numeroDeCelularesAEnviar > celulares.length) {
            throw new IllegalArgumentException("Número de celulares a enviar inválido.");
        }
        if (bytesPorAtributo == null || bytesPorAtributo.length != 3) {
            throw new IllegalArgumentException("Array 'bytesPorAtributo' deve ter exatamente 3 elementos.");
        }
        if (bytesPorAtributo[2] != 4 && bytesPorAtributo[2] != Integer.BYTES) {
            System.err.println("Aviso: bytesPorAtributo[2] (para int armazenamentoGB) idealmente deveria ser 4. Usando o valor fornecido: " + bytesPorAtributo[2]);
        }
        this.celulares = celulares;
        this.numeroDeCelularesAEnviar = numeroDeCelularesAEnviar;
        this.bytesPorAtributo = bytesPorAtributo;
        this.destino = destino;
    }

    private boolean prepararProximoBuffer() {
        if (bufferAtributoAtual != null && byteAtualNoAtributo < bufferAtributoAtual.length) {
            return true;
        }

        byteAtualNoAtributo = 0;

        atributoAtualIndex++;
        if (atributoAtualIndex >= 3) {
            atributoAtualIndex = 0;
            celularAtualIndex++;
        }

        if (celularAtualIndex >= numeroDeCelularesAEnviar) {
            return false;
        }

        Celular celularAtual = celulares[celularAtualIndex];
        if (celularAtual == null) {
            celularAtualIndex++;
            atributoAtualIndex = -1;
            return prepararProximoBuffer();
        }

        switch (atributoAtualIndex) {
            case 0:
                String codigo = celularAtual.getCodigo() != null ? celularAtual.getCodigo() : "";
                bufferAtributoAtual = preencherOuTruncarString(codigo, bytesPorAtributo[0]);
                break;
            case 1:
                String modelo = celularAtual.getModelo() != null ? celularAtual.getModelo() : "";
                bufferAtributoAtual = preencherOuTruncarString(modelo, bytesPorAtributo[1]);
                break;
            case 2:
                int armazenamento;
                try {
                    String memoriaStr = celularAtual.getMemoriaInterna();
                    if (memoriaStr == null) memoriaStr = "0";
                    armazenamento = Integer.parseInt(memoriaStr.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException | NullPointerException e) {
                    armazenamento = 0;
                }
                bufferAtributoAtual = intParaBytes(armazenamento);
                if (bytesPorAtributo[2] != 4) {
                     byte[] tempBuffer = new byte[bytesPorAtributo[2]];
                     int bytesACopiar = Math.min(4, bytesPorAtributo[2]);
                     System.arraycopy(bufferAtributoAtual, 0, tempBuffer, 0, bytesACopiar);
                     bufferAtributoAtual = tempBuffer;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private byte[] preencherOuTruncarString(String str, int tamanhoMaxBytes) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[tamanhoMaxBytes];
        Arrays.fill(buffer, (byte) 0);
        int bytesACopiar = Math.min(strBytes.length, tamanhoMaxBytes);
        System.arraycopy(strBytes, 0, buffer, 0, bytesACopiar);
        return buffer;
    }

    private byte[] intParaBytes(int valor) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (valor >> 24);
        bytes[1] = (byte) (valor >> 16);
        bytes[2] = (byte) (valor >> 8);
        bytes[3] = (byte) valor;
        return bytes;
    }

    @Override
    public void write(int b) throws IOException {
        if (bufferAtributoAtual == null || byteAtualNoAtributo >= bufferAtributoAtual.length) {
            if (!prepararProximoBuffer()) {
                return;
            }
        }
        destino.write(bufferAtributoAtual[byteAtualNoAtributo++]);
    }

    public void enviarTodosOsCelulares() throws IOException {

        while (prepararProximoBuffer()) { 
            if (bufferAtributoAtual != null) {
                destino.write(bufferAtributoAtual, 0, bufferAtributoAtual.length);
                byteAtualNoAtributo = bufferAtributoAtual.length;
            }
        }
        flush();
    }

    @Override
    public void close() throws IOException {
        if (destino != System.out && destino != System.err) {
            destino.close();
        } else {
            flush(); 
        }
        super.close();
    }
}
