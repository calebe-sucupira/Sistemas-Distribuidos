package com.cliente;

import com.entidades.Produto;
import com.entidades.Venda;
import com.interfaces.ILojaService;
import com.rmi_protocol.Marshaller;
import com.rmi_protocol.Mensagem;
import com.rmi_protocol.Requestor;
import com.google.gson.reflect.TypeToken; 

import java.lang.reflect.Type; 
import java.rmi.RemoteException;
import java.util.List;

public class LojaServiceStub implements ILojaService {

    private final Requestor requestor;
    private final Marshaller marshaller = new Marshaller();
    private final String objectReference = "LojaService";

    public LojaServiceStub(String host, int port) {
        this.requestor = new Requestor(host, port);
    }

    @Override
    public Produto consultarProduto(String codigo) throws RemoteException {
        try {
            byte[] args = marshaller.empacotar(codigo);
            Mensagem resposta = requestor.doOperation(objectReference, "consultarProduto", args);
            return marshaller.desempacotar(resposta.getArguments(), Produto.class);
        } catch (Exception e) {
            throw new RemoteException("Erro ao consultar produto", e);
        }
    }

    @Override
    public String adicionarProduto(Produto p) throws RemoteException {
        try {
            byte[] args = marshaller.empacotar(p, Produto.class);

            Mensagem resposta = requestor.doOperation(objectReference, "adicionarProduto", args);
            return marshaller.desempacotar(resposta.getArguments(), String.class);
        } catch (Exception e) {
            throw new RemoteException("Erro ao adicionar produto", e);
        }
    }

    @Override
    public List<Produto> listarEstoque() throws RemoteException {
        try {
            Mensagem resposta = requestor.doOperation(objectReference, "listarEstoque", new byte[0]);
            
            Type tipoListaDeProdutos = new TypeToken<List<Produto>>() {}.getType();
            return marshaller.desempacotar(resposta.getArguments(), tipoListaDeProdutos);
            
        } catch (Exception e) {
            throw new RemoteException("Erro ao listar estoque", e);
        }
    }

    @Override
    public String realizarVenda(Venda v) throws RemoteException {
        try {
            byte[] args = marshaller.empacotar(v);
            Mensagem resposta = requestor.doOperation(objectReference, "realizarVenda", args);
            return marshaller.desempacotar(resposta.getArguments(), String.class);
        } catch (Exception e) {
            throw new RemoteException("Erro ao realizar venda", e);
        }
    }
}