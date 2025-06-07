package com.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date data;
    private Vendedor vendedor;          
    private List<Produto> itensVendidos; 

    public Venda(int id, Vendedor vendedor, List<Produto> itensVendidos) {
        this.id = id;
        this.vendedor = vendedor;
        this.itensVendidos = itensVendidos;
        this.data = new Date(); 
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public List<Produto> getItensVendidos() {
        return itensVendidos;
    }

    public void setItensVendidos(List<Produto> itensVendidos) {
        this.itensVendidos = itensVendidos;
    }

    @Override
    public String toString() {
        return "Venda [id=" + id + ", data=" + data + ", vendedor=" + vendedor.getNomeCompleto() + ", itens=" + itensVendidos.size() + "]";
    }
}