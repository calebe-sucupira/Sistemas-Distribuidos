package com.cliente;

import com.entidades.Celular;
import com.entidades.Produto;
import com.entidades.Venda;
import com.entidades.Vendedor;
import com.interfaces.ILojaService;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("Iniciando cliente...");

        try {
            ILojaService loja = new LojaServiceStub("localhost", 6789);

            System.out.println("\n--- Testando 'listarEstoque' ---");
            List<Produto> estoque = loja.listarEstoque();
            System.out.println("Produtos em estoque: " + estoque);

            System.out.println("\n--- Testando 'consultarProduto' ---");
            Produto p = loja.consultarProduto("SAM01");
            if (p != null) {
                System.out.println("Produto encontrado: " + p);
            } else {
                System.out.println("Produto com código SAM01 não encontrado.");
            }

            System.out.println("\n--- Testando 'adicionarProduto' ---");
            Produto novoCelular = new Celular("APP01", "iPhone 16", "Apple", 9999.99, "16 Pro Max", "Titânio", "8GB", "iOS 18");
            String resultadoAdicao = loja.adicionarProduto(novoCelular);
            System.out.println("Resultado: " + resultadoAdicao);
        
            System.out.println("Listando estoque novamente...");
            estoque = loja.listarEstoque();
            System.out.println("Produtos em estoque agora: " + estoque.size());


            System.out.println("\n--- Testando 'realizarVenda' ---");
            Vendedor vendedor = new Vendedor("V01", "Calebe Sucupira", "123.456.789-00", "07/06/2025");
            List<Produto> itensVenda = new ArrayList<>();
            itensVenda.add(p); 
            Venda novaVenda = new Venda(1, vendedor, itensVenda);
            String resultadoVenda = loja.realizarVenda(novaVenda);
            System.out.println("Resultado: " + resultadoVenda);


        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}