package com.loja;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LojaService {
    private final List<Produto> estoque = new CopyOnWriteArrayList<>();

    public LojaService() {
        estoque.add(new Produto("iPhone 15 Pro", "Apple", 7500.00));
        estoque.add(new Produto("Galaxy S24 Ultra", "Samsung", 7100.00));
        estoque.add(new Produto("Capa de Silicone para iPhone 15", "Apple", 299.00));
        estoque.add(new Produto("Película de Vidro 3D", "Spigen", 120.00));
        estoque.add(new Produto("PowerBank 10000mAh", "Anker", 180.00));
    }

    public List<Produto> listarTodos() {
        return estoque;
    }

    public Optional<Produto> buscarPorId(int id) {
        return estoque.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Produto adicionarProduto(Produto produtoRecebido) {
        produtoRecebido.setId(Produto.gerarProximoId());

        estoque.add(produtoRecebido);
        return produtoRecebido;
    }

    public Produto atualizarProduto(int id, Produto produtoAtualizado) {
        Optional<Produto> produtoOpt = buscarPorId(id);
        if (produtoOpt.isPresent()) {
            Produto produtoExistente = produtoOpt.get();
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setMarca(produtoAtualizado.getMarca());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            return produtoExistente;
        } else {
            return null;
        }
    }

    public boolean deletarProduto(int id) {
        return estoque.removeIf(p -> p.getId() == id);
    }
}