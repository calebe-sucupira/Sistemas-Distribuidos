package com.entidades;

public class Capa extends Produto { 

    private static final long serialVersionUID = 1L;

    private String tipoMaterial;
    private String cor;
    private String compatibilidadeModeloCelular;
    private String caracteristicasAdicionais; 

    public Capa(String codigo, String nome, String marca, double preco,
                String tipoMaterial, String cor, String compatibilidadeModeloCelular, String caracteristicasAdicionais) { 
        super(codigo, nome, marca, preco); 
        this.tipoMaterial = tipoMaterial;
        this.cor = cor;
        this.compatibilidadeModeloCelular = compatibilidadeModeloCelular;
        this.caracteristicasAdicionais = caracteristicasAdicionais;
    }

    
    public Capa(String codigo, String nome, String marca, double preco,
                String tipoMaterial, String cor, String compatibilidadeModeloCelular) {
        this(codigo, nome, marca, preco, tipoMaterial, cor, compatibilidadeModeloCelular, null);
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getCompatibilidadeModeloCelular() {
        return compatibilidadeModeloCelular;
    }

    public void setCompatibilidadeModeloCelular(String compatibilidadeModeloCelular) {
        this.compatibilidadeModeloCelular = compatibilidadeModeloCelular;
    }

    public String getCaracteristicasAdicionais() {
        return caracteristicasAdicionais;
    }

    public void setCaracteristicasAdicionais(String caracteristicasAdicionais) {
        this.caracteristicasAdicionais = caracteristicasAdicionais;
    }

    @Override
    public String toString() {
        String infoAdicional = (caracteristicasAdicionais != null && !caracteristicasAdicionais.isEmpty()) ?
                               ", caracteristicasAdicionais=" + caracteristicasAdicionais : "";
        return "Capa [" +
               "codigo=" + getCodigo() +
               ", nome=" + getNome() +
               ", marca=" + getMarca() +
               ", preco=" + getPreco() +
               ", tipoMaterial=" + tipoMaterial +
               ", cor=" + cor +
               ", compatibilidadeModeloCelular=" + compatibilidadeModeloCelular +
               infoAdicional +
               "]";
    }
}