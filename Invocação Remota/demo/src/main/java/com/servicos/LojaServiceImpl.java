package com.servicos;

import com.entidades.Celular;
import com.entidades.Capa;
import com.entidades.Produto;
import com.interfaces.ILojaService;
import com.entidades.Venda;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LojaServiceImpl implements ILojaService {

    private final Map<String, Produto> estoque = new ConcurrentHashMap<>();

    public LojaServiceImpl() {
        Produto celular1 = new Celular("SAM01", "Galaxy S25", "Samsung", 5999.99, "S25 Ultra", "Azul", "16GB", "Android 16");
        Produto capa1 = new Capa("CAP01", "Capa de Silicone S25", "Samsung", 149.99, "Silicone", "Preta", "Galaxy S25", "Anti-impacto");
        
        estoque.put(celular1.getCodigo(), celular1);
        estoque.put(capa1.getCodigo(), capa1);
    }

    @Override
    public Produto consultarProduto(String codigo) throws RemoteException {
        System.out.println("LOG DO SERVIDOR: Recebido pedido para consultar produto com código: " + codigo);
        return estoque.get(codigo);
    }

    @Override
    public String adicionarProduto(Produto p) throws RemoteException {
        System.out.println("LOG DO SERVIDOR: Recebido pedido para adicionar produto: " + p.getNome());
        if (p != null && p.getCodigo() != null) {
            estoque.put(p.getCodigo(), p);
            return "Produto adicionado com sucesso!";
        }
        return "Falha ao adicionar produto.";
    }

    @Override
    public List<Produto> listarEstoque() throws RemoteException {
        System.out.println("LOG DO SERVIDOR: Recebido pedido para listar estoque.");
        return new ArrayList<>(estoque.values());
    }

    @Override
    public String realizarVenda(Venda v) throws RemoteException {
        System.out.println("LOG DO SERVIDOR: Recebido pedido para realizar venda para " + v.getVendedor().getNomeCompleto());
        if (v != null && v.getItensVendidos() != null && !v.getItensVendidos().isEmpty()) {
            return "Venda ID " + v.getId() + " registrada com sucesso!";
        }
        return "Dados da venda inválidos.";
    }
}