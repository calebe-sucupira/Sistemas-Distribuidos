package com.entidades;

public class Pelicula extends Produto {

    private static final long serialVersionUID = 1L;

    private String tipoMaterial;
    private String compatibilidadeModeloCelular;

    public Pelicula(String codigo, String nome, String marca, double preco,
                    String tipoMaterial, String compatibilidadeModeloCelular, String acabamento) {
        super(codigo, nome, marca, preco);
        this.tipoMaterial = tipoMaterial;
        this.compatibilidadeModeloCelular = compatibilidadeModeloCelular;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getCompatibilidadeModeloCelular() {
        return compatibilidadeModeloCelular;
    }

    public void setCompatibilidadeModeloCelular(String compatibilidadeModeloCelular) {
        this.compatibilidadeModeloCelular = compatibilidadeModeloCelular;
    }

    @Override
    public String toString() {
        return "Pelicula [" +
               "codigo=" + getCodigo() +
               ", nome=" + getNome() +
               ", marca=" + getMarca() +
               ", preco=" + getPreco() +
               ", tipoMaterial=" + tipoMaterial +
               ", compatibilidadeModeloCelular=" + compatibilidadeModeloCelular +
               "]";
    }
}