package service;

import entidades.Produto;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorEstoqueServico {

    private Map<String, Integer> stock;

    public GerenciadorEstoqueServico() {
        this.stock = new HashMap<>();
    }

    public void adicionarProdutoAoEstoque(Produto produto, int quantidade) {
        if (produto == null || produto.getCodigo() == null || produto.getCodigo().trim().isEmpty()) {
            System.err.println("Erro: Produto ou código do produto inválido para adicionar ao stock.");
            return;
        }
        if (quantidade < 0) {
            System.err.println("Erro: Quantidade a ser adicionada ao stock não pode ser negativa.");
            return;
        }

        String codigoProduto = produto.getCodigo();
        this.stock.merge(codigoProduto, quantidade, Integer::sum);
        System.out.println("Stock atualizado para " + produto.getNome() + " (Código: " + codigoProduto + "): " + this.stock.get(codigoProduto) + " unidades.");
    }

    public boolean verificarDisponibilidade(Produto produto, int quantidadeDesejada) {
        if (produto == null || produto.getCodigo() == null) {
            System.err.println("Erro: Produto inválido para verificar disponibilidade.");
            return false;
        }
        if (quantidadeDesejada <= 0) {
            System.err.println("Erro: Quantidade desejada deve ser maior que zero.");
            return false;
        }

        String codigoProduto = produto.getCodigo();
        int quantidadeAtual = this.stock.getOrDefault(codigoProduto, 0);

        return quantidadeAtual >= quantidadeDesejada;
    }

    public boolean baixarEstoque(Produto produto, int quantidadeVendida) {
        if (produto == null || produto.getCodigo() == null) {
            System.err.println("Erro: Produto inválido para dar baixa no stock.");
            return false;
        }
        if (quantidadeVendida <= 0) {
            System.err.println("Erro: Quantidade vendida deve ser maior que zero para dar baixa no stock.");
            return false;
        }

        String codigoProduto = produto.getCodigo();
        if (!verificarDisponibilidade(produto, quantidadeVendida)) {
            System.err.println("Stock insuficiente para " + produto.getNome() + ". Baixa não realizada.");
            return false;
        }

        int quantidadeAtual = this.stock.get(codigoProduto);
        this.stock.put(codigoProduto, quantidadeAtual - quantidadeVendida);
        System.out.println("Baixa de " + quantidadeVendida + " unidade(s) de " + produto.getNome() + " (Código: " + codigoProduto + ") realizada. Stock restante: " + this.stock.get(codigoProduto));
        return true;
    }

    public int consultarEstoque(Produto produto) {
        if (produto == null || produto.getCodigo() == null) {
            System.err.println("Erro: Produto inválido para consultar stock.");
            return 0;
        }
        return this.stock.getOrDefault(produto.getCodigo(), 0);
    }

    public void inicializarStockExemplo() {
        System.out.println("Stock de exemplo inicializado.");
    }

    public void exibirStockAtual() {
        System.out.println("\n--- Stock Atual ---");
        if (stock.isEmpty()) {
            System.out.println("Stock vazio.");
            return;
        }
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            System.out.println("Código Produto: " + entry.getKey() + " - Quantidade: " + entry.getValue());
        }
        System.out.println("--------------------");
    }
}
