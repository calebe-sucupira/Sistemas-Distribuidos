package com.relogios;

public class Mensagem {
    private final String conteudo;
    private final Object relogio;

    public Mensagem(String conteudo, Object relogio) {
        this.conteudo = conteudo;
        this.relogio = relogio;
    }

    public String getConteudo() {
        return conteudo;
    }

    public Object getRelogio() {
        return relogio;
    }
}