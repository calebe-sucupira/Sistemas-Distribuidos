package com.relogios;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class Processo extends Thread {
    private final int id;
    private final TipoRelogio tipoRelogio;
    private final int totalProcessos;

    private int relogioLamport = 0;
    private final int[] relogioVetorial;

    private final BlockingQueue<Mensagem> filaDeMensagens;
    private final Processo[] todosProcessos;

    public Processo(int id, TipoRelogio tipo, int totalProcessos, BlockingQueue<Mensagem> fila, Processo[] todos) {
        this.id = id;
        this.tipoRelogio = tipo;
        this.totalProcessos = totalProcessos;
        this.filaDeMensagens = fila;
        this.todosProcessos = todos;
        this.relogioVetorial = new int[totalProcessos];
    }

    private void eventoLocal() {
        atualizarRelogio(null);
        logEvento("Evento Local");
    }

    public void enviarMensagem(int idDestino) {
        atualizarRelogio(null);

        Object relogioAtual = getRelogioAtual();
        Mensagem msg = new Mensagem("Olá do processo " + this.id, relogioAtual);

        logEvento("ENVIOU para P" + idDestino);

        try {
            Thread.sleep((long) (Math.random() * 100));
            todosProcessos[idDestino].getFilaDeMensagens().put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        eventoLocal();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Mensagem msg = filaDeMensagens.take();
                atualizarRelogio(msg.getRelogio());
                logEvento("RECEBEU de P?");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void atualizarRelogio(Object relogioRecebido) {
        switch (tipoRelogio) {
            case LAMPORT:
                if (relogioRecebido != null) {
                    relogioLamport = Math.max(relogioLamport, (Integer) relogioRecebido) + 1;
                } else {
                    relogioLamport++;
                }
                break;
            case VETORIAL:
                relogioVetorial[id]++;
                if (relogioRecebido != null) {
                    int[] vetorRecebido = (int[]) relogioRecebido;
                    for (int i = 0; i < totalProcessos; i++) {
                        relogioVetorial[i] = Math.max(relogioVetorial[i], vetorRecebido[i]);
                    }
                }
                break;
            case FISICO:
            default:
                break;
        }
    }

    private Object getRelogioAtual() {
        switch (tipoRelogio) {
            case LAMPORT:
                return relogioLamport;
            case VETORIAL:
                return Arrays.copyOf(relogioVetorial, relogioVetorial.length);
            case FISICO:
            default:
                return System.currentTimeMillis();
        }
    }

    private void logEvento(String evento) {
        Object relogio = getRelogioAtual();
        String relogioStr;

        if (relogio instanceof int[]) {
            relogioStr = Arrays.toString((int[]) relogio);
        } else {
            relogioStr = relogio.toString();
        }

        System.out.printf("Processo P%d | Evento: %-20s | Relógio (%s): %s%n",
                id, evento, tipoRelogio, relogioStr);
    }

    public BlockingQueue<Mensagem> getFilaDeMensagens() {
        return filaDeMensagens;
    }
}