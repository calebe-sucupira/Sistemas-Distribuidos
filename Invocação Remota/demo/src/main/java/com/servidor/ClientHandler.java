package com.servidor;

import com.entidades.Produto;
import com.entidades.Venda;
import com.interfaces.ILojaService;
import com.rmi_protocol.Marshaller;
import com.rmi_protocol.Mensagem;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import com.google.gson.reflect.TypeToken; 
import java.lang.reflect.Type;         


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ILojaService lojaService;
    private final Marshaller marshaller = new Marshaller();

    public ClientHandler(Socket socket, ILojaService service) {
        this.clientSocket = socket;
        this.lojaService = service;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            Mensagem requisicao = (Mensagem) in.readObject(); 
            System.out.println("Servidor: Recebida requisição ID " + requisicao.getRequestId());

            Mensagem resposta = processarRequisicao(requisicao);

            out.writeObject(resposta); 
            out.flush();

        } catch (Exception e) {
            System.err.println("Erro no ClientHandler: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
            }
        }
    }

    private Mensagem processarRequisicao(Mensagem requisicao) throws Exception {
        String methodId = requisicao.getMethodId();
        byte[] packedArgs = requisicao.getArguments();
        byte[] packedResult;

        switch (methodId) {
            case "consultarProduto":
                String codigo = marshaller.desempacotar(packedArgs, String.class);
                Produto p = lojaService.consultarProduto(codigo);
                packedResult = marshaller.empacotar(p, Produto.class);
                break;
            case "adicionarProduto":
                Produto produtoNovo = marshaller.desempacotar(packedArgs, Produto.class);
                String resultAdicionar = lojaService.adicionarProduto(produtoNovo);
                packedResult = marshaller.empacotar(resultAdicionar);
                break;
            case "listarEstoque":
                List<Produto> estoque = lojaService.listarEstoque();
                Type tipoListaDeProdutos = new TypeToken<List<Produto>>() {}.getType();
                packedResult = marshaller.empacotar(estoque, tipoListaDeProdutos);
                break;
            case "realizarVenda":
                Venda v = marshaller.desempacotar(packedArgs, Venda.class);
                String resultVenda = lojaService.realizarVenda(v);
                packedResult = marshaller.empacotar(resultVenda);
                break;
            default:
                String erro = "Método desconhecido: " + methodId;
                packedResult = marshaller.empacotar(erro);
                break;
        }

        return new Mensagem(1, requisicao.getRequestId(), requisicao.getObjectReference(), methodId, packedResult);
    }
}