package service;

import entidades.Produto;
import entidades.Vendedor;

public class RegistroVendasServico implements Vendas {

    private GerenciadorEstoqueServico gerenciadorEstoque;

    public RegistroVendasServico() {
        this.gerenciadorEstoque = new GerenciadorEstoqueServico();
    }

    public RegistroVendasServico(GerenciadorEstoqueServico gerenciadorEstoque) {
        this.gerenciadorEstoque = gerenciadorEstoque;
    }

    @Override
    public boolean registrarVenda(Produto produto, int quantidade, Vendedor vendedor) {
        if (produto == null || vendedor == null) {
            System.err.println("Erro: Produto ou Vendedor não pode ser nulo para registrar a venda.");
            return false;
        }
        if (quantidade <= 0) {
            System.err.println("Erro: A quantidade deve ser maior que zero para registrar a venda.");
            return false;
        }

        if (!gerenciadorEstoque.verificarDisponibilidade(produto, quantidade)) {
            System.out.println(">>> ATENÇÃO: Stock insuficiente para o produto: " + produto.getNome() + " (Código: " + produto.getCodigo() + "). Venda não realizada. <<<");
            System.out.println("Disponível: " + gerenciadorEstoque.consultarEstoque(produto) + ", Desejado: " + quantidade);
            return false;
        }

        double totalVenda = calcularTotalVenda(produto, quantidade);

        boolean baixaOk = gerenciadorEstoque.baixarEstoque(produto, quantidade);
        if (!baixaOk) {
            System.err.println("Erro crítico ao tentar dar baixa no stock para o produto: " + produto.getNome() + ". Venda cancelada.");
            return false;
        }

        String registoVenda = "--- Venda Registada com Sucesso ---" +
                              "\n  Produto: " + produto.getNome() + " (Código: " + produto.getCodigo() + ")" +
                              "\n  Quantidade: " + quantidade +
                              "\n  Preço Unitário: " + String.format("R$ %.2f", produto.getPreco()) +
                              "\n  Valor Total: " + String.format("R$ %.2f", totalVenda) +
                              "\n  Vendedor: " + vendedor.getNomeCompleto() + " (ID: " + vendedor.getIdVendedor() + ")";

        System.out.println(registoVenda);
        System.out.println("------------------------------------");

        return true;
    }

    @Override
    public double calcularTotalVenda(Produto produto, int quantidade) {
        if (produto != null && quantidade > 0) {
            return produto.getPreco() * quantidade;
        }
        System.err.println("Aviso: Produto nulo ou quantidade inválida para calcular o total da venda.");
        return 0.0;
    }

    public GerenciadorEstoqueServico getGerenciadorEstoque() {
        return gerenciadorEstoque;
    }
}
