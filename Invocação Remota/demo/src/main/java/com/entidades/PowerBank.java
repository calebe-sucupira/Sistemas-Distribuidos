package com.entidades;

public class PowerBank extends Produto {

    private static final long serialVersionUID = 1L;

    private int capacidadeMah;
    private int numeroPortasUSB;
    private String tipoPortas;
    private String tecnologiaCarregamentoRapido;

    public PowerBank(String codigo, String nome, String marca, double preco,
                     int capacidadeMah, int numeroPortasUSB, String tipoPortas, String tecnologiaCarregamentoRapido) {
        super(codigo, nome, marca, preco);
        this.capacidadeMah = capacidadeMah;
        this.numeroPortasUSB = numeroPortasUSB;
        this.tipoPortas = tipoPortas;
        this.tecnologiaCarregamentoRapido = tecnologiaCarregamentoRapido;
    }

    public PowerBank(String codigo, String nome, String marca, double preco,
                     int capacidadeMah, int numeroPortasUSB, String tipoPortas) {
        this(codigo, nome, marca, preco, capacidadeMah, numeroPortasUSB, tipoPortas, null);
    }

    public int getCapacidadeMah() {
        return capacidadeMah;
    }

    public void setCapacidadeMah(int capacidadeMah) {
        this.capacidadeMah = capacidadeMah;
    }

    public int getNumeroPortasUSB() {
        return numeroPortasUSB;
    }

    public void setNumeroPortasUSB(int numeroPortasUSB) {
        this.numeroPortasUSB = numeroPortasUSB;
    }

    public String getTipoPortas() {
        return tipoPortas;
    }

    public void setTipoPortas(String tipoPortas) {
        this.tipoPortas = tipoPortas;
    }

    public String getTecnologiaCarregamentoRapido() {
        return tecnologiaCarregamentoRapido;
    }

    public void setTecnologiaCarregamentoRapido(String tecnologiaCarregamentoRapido) {
        this.tecnologiaCarregamentoRapido = tecnologiaCarregamentoRapido;
    }

    @Override
    public String toString() {
        String infoTecnologia = (tecnologiaCarregamentoRapido != null && !tecnologiaCarregamentoRapido.isEmpty()) ?
                                 ", tecnologiaCarregamentoRapido=" + tecnologiaCarregamentoRapido : "";


        return "PowerBank [" +
               "codigo=" + getCodigo() +
               ", nome=" + getNome() +
               ", marca=" + getMarca() +
               ", preco=" + getPreco() +
               ", capacidadeMah=" + capacidadeMah +
               ", numeroPortasUSB=" + numeroPortasUSB +
               ", tipoPortas='" + tipoPortas + '\'' +
               infoTecnologia +

               "]";
    }
}