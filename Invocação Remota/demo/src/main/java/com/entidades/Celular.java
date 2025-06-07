package com.entidades;

public class Celular extends Produto { 

    private static final long serialVersionUID = 1L;

    private String modelo;
    private String color; 
    private String memoriaInterna; 
    private String memoriaRam;    

    public Celular(String codigo, String nome, String marca, double preco,
                   String modelo, String color, String memoriaInterna, String memoriaRam) {
        super(codigo, nome, marca, preco);
        this.modelo = modelo;
        this.color = color;
        this.memoriaInterna = memoriaInterna;
        this.memoriaRam = memoriaRam;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMemoriaInterna() {
        return memoriaInterna;
    }

    public void setMemoriaInterna(String memoriaInterna) {
        this.memoriaInterna = memoriaInterna;
    }

    public String getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(String memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    @Override
    public String toString() {
        return "Celular [" +
               "codigo=" + getCodigo() + 
               ", nome=" + getNome() +
               ", marca=" + getMarca() +
               ", preco=" + getPreco() +
               ", modelo=" + modelo +
               ", color=" + color +
               ", memoriaInterna=" + memoriaInterna +
               ", memoriaRam=" + memoriaRam +
               "]";
    }
}