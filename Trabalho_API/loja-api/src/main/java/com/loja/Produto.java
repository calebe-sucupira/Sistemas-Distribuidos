package com.loja;

public class Produto {
    private static int proximoId = 1;

    private int id;
    private String nome;
    private String marca;
    private double preco;

    public static int gerarProximoId() {
        return proximoId++;
    }

    public Produto() {
    }

    public Produto(String nome, String marca, double preco) {
        this.id = gerarProximoId();
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}