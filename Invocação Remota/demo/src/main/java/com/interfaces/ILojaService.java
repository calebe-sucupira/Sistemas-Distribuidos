package com.interfaces;

import com.entidades.Produto;
import com.entidades.Venda;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILojaService extends Remote {

    Produto consultarProduto(String codigo) throws RemoteException;

    String adicionarProduto(Produto p) throws RemoteException;

    List<Produto> listarEstoque() throws RemoteException;
    
    String realizarVenda(Venda v) throws RemoteException;
}