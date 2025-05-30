package service;

import entidades.Produto;
import entidades.Vendedor;

public interface Vendas {

    boolean registrarVenda(Produto produto, int quantidade, Vendedor vendedor);

    double calcularTotalVenda(Produto produto, int quantidade);

}
