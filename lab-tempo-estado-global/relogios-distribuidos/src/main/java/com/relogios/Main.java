package com.relogios;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        final TipoRelogio TIPO_RELOGIO = TipoRelogio.VETORIAL;

        System.out.println("--- Iniciando Simulação com Relógio: " + TIPO_RELOGIO + " ---");

        int totalProcessos = 3;
        Processo[] processos = new Processo[totalProcessos];
        @SuppressWarnings("unchecked")
        BlockingQueue<Mensagem>[] filas = new ArrayBlockingQueue[totalProcessos];

        for (int i = 0; i < totalProcessos; i++) {
            filas[i] = new ArrayBlockingQueue<>(10);
        }

        for (int i = 0; i < totalProcessos; i++) {
            processos[i] = new Processo(i, TIPO_RELOGIO, totalProcessos, filas[i], processos);
            processos[i].start();
        }

        Thread.sleep(500);

        processos[0].enviarMensagem(1);
        Thread.sleep(200);

        processos[2].enviarMensagem(0);
        Thread.sleep(200);

        processos[1].enviarMensagem(2);
        Thread.sleep(1000);
        for (int i = 0; i < totalProcessos; i++) {
            processos[i].interrupt();
        }

    }
}