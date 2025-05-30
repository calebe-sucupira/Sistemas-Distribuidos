package network;

import entidades.Capa;
import entidades.Celular;
import entidades.Pelicula;
import entidades.PowerBank;
import entidades.Produto;
import entidades.Vendedor;
import service.GerenciadorEstoqueServico;
import service.RegistroVendasServico;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServidorLojaTCP {

    private int porta;
    private Map<String, Produto> catalogoProdutos;
    private Map<String, Vendedor> catalogoVendedores;
    private GerenciadorEstoqueServico gerenciadorEstoque;
    private RegistroVendasServico registroVendasServico;

    public ServidorLojaTCP(int porta) {
        this.porta = porta;
        this.catalogoProdutos = new HashMap<>();
        this.catalogoVendedores = new HashMap<>();
        this.gerenciadorEstoque = new GerenciadorEstoqueServico();
        this.registroVendasServico = new RegistroVendasServico(this.gerenciadorEstoque);
        inicializarCatalogosEStockExemplo();
    }

    private void inicializarCatalogosEStockExemplo() {
        System.out.println("Servidor: Iniciando a carga do catálogo e estoque...");

        Celular s23Ultra = new Celular("SAM001", "Galaxy S23 Ultra", "Samsung", 5500.00, "S23 Ultra 5G", "Phantom Black", "512GB", "12GB");
        Celular s24Plus = new Celular("SAM002", "Galaxy S24+", "Samsung", 4800.00, "S24+ 5G", "Onyx Black", "256GB", "8GB");
        Celular a55 = new Celular("SAM003", "Galaxy A55", "Samsung", 2200.00, "A55 5G", "Awesome Iceblue", "128GB", "8GB");
        catalogoProdutos.put(s23Ultra.getCodigo(), s23Ultra);
        catalogoProdutos.put(s24Plus.getCodigo(), s24Plus);
        catalogoProdutos.put(a55.getCodigo(), a55);
        gerenciadorEstoque.adicionarProdutoAoEstoque(s23Ultra, 8);
        gerenciadorEstoque.adicionarProdutoAoEstoque(s24Plus, 15);
        gerenciadorEstoque.adicionarProdutoAoEstoque(a55, 20);

        Capa capaS23Ultra = new Capa("CAP001", "Capa Silicone S23 Ultra", "Samsung", 120.00, "Silicone", "Preta", "Galaxy S23 Ultra", "Proteção Suave");
        Capa capaA55 = new Capa("CAP002", "Capa Anti-Impacto A55", "Spigen", 150.00, "TPU e Policarbonato", "Transparente", "Galaxy A55", "Air Cushion Technology");
        catalogoProdutos.put(capaS23Ultra.getCodigo(), capaS23Ultra);
        catalogoProdutos.put(capaA55.getCodigo(), capaA55);
        gerenciadorEstoque.adicionarProdutoAoEstoque(capaS23Ultra, 30);
        gerenciadorEstoque.adicionarProdutoAoEstoque(capaA55, 25);

        Pelicula peliculaS23Ultra = new Pelicula("PEL001", "Película Vidro S23 Ultra", "GlassPro", 80.00, "Vidro Temperado 9H", "Galaxy S23 Ultra", "Ultra Transparente");
        Pelicula peliculaA55 = new Pelicula("PEL002", "Película Hidrogel A55", "HydroFilm", 65.00, "Hidrogel Flexível", "Galaxy A55", "Auto-reparadora");
        catalogoProdutos.put(peliculaS23Ultra.getCodigo(), peliculaS23Ultra);
        catalogoProdutos.put(peliculaA55.getCodigo(), peliculaA55);
        gerenciadorEstoque.adicionarProdutoAoEstoque(peliculaS23Ultra, 40);
        gerenciadorEstoque.adicionarProdutoAoEstoque(peliculaA55, 35);

        PowerBank pb10k = new PowerBank("PB001", "PowerBank Samsung 10000mAh", "Samsung", 180.00, 10000, 2, "1x USB-A, 1x USB-C", "Fast Charge 15W");
        PowerBank pb20k = new PowerBank("PB002", "PowerBank Baseus 20000mAh", "Baseus", 250.00, 20000, 3, "2x USB-A, 1x USB-C PD", "Power Delivery 20W");
        catalogoProdutos.put(pb10k.getCodigo(), pb10k);
        catalogoProdutos.put(pb20k.getCodigo(), pb20k);
        gerenciadorEstoque.adicionarProdutoAoEstoque(pb10k, 15);
        gerenciadorEstoque.adicionarProdutoAoEstoque(pb20k, 10);

        Vendedor calebe = new Vendedor("V101", "Calebe Sucupira", "111.000.111-00", "10/01/2022");
        Vendedor josue = new Vendedor("V102", "Josué Sucupira", "222.000.222-00", "05/06/2023");
        catalogoVendedores.put(calebe.getIdVendedor(), calebe);
        catalogoVendedores.put(josue.getIdVendedor(), josue);

        System.out.println("Servidor: Catálogos de produtos, vendedores e stock inicializados com exemplos.");
        gerenciadorEstoque.exibirStockAtual();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("ServidorLojaTCP iniciado na porta " + porta + ". Aguardando conexões de clientes...");
            while (true) {
                Socket socketCliente = null;
                try {
                    socketCliente = serverSocket.accept();
                    System.out.println("Servidor: Cliente conectado: " + socketCliente.getInetAddress().getHostAddress() + ":" + socketCliente.getPort());
                    DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socketCliente.getOutputStream());
                    while (socketCliente.isConnected() && !socketCliente.isClosed()) {
                        System.out.println("Servidor: Aguardando operação do cliente " + socketCliente.getInetAddress().getHostAddress());
                        if (!processarRequisicaoCliente(dis, dos)) {
                            break;
                        }
                    }
                } catch (SocketException se) {
                    System.out.println("Servidor: SocketException (cliente pode ter desconectado abruptamente): " + se.getMessage());
                } catch (EOFException eof) {
                    System.out.println("Servidor: Cliente desconectou (EOFException).");
                } catch (IOException e) {
                    System.err.println("Servidor: Erro de I/O na thread de tratamento do cliente ou ao aceitar conexão: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (socketCliente != null && !socketCliente.isClosed()) {
                        try {
                            System.out.println("Servidor: Fechando conexão com o cliente " + socketCliente.getInetAddress().getHostAddress());
                            socketCliente.close();
                        } catch (IOException ex) {
                            System.err.println("Servidor: Erro ao fechar socket do cliente: " + ex.getMessage());
                        }
                    }
                    System.out.println("Servidor: Pronto para aceitar nova conexão de cliente.");
                    System.out.println("----------------------------------------------------");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro crítico ao iniciar o ServidorLojaTCP na porta " + porta + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean processarRequisicaoCliente(DataInputStream dis, DataOutputStream dos) {
        byte tipoOperacao;
        try {
            tipoOperacao = dis.readByte();
            System.out.println("Servidor: Operação recebida do cliente: " + tipoOperacao);
        } catch (EOFException | SocketException e) {
            System.out.println("Servidor: Cliente desconectou (" + e.getClass().getSimpleName() + ") ao ler tipo de operação.");
            return false;
        } catch (IOException e) {
            System.err.println("Servidor: Erro de I/O ao ler tipo de operação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            switch (tipoOperacao) {
                case ProtocoloEmpacotamento.OP_CONSULTAR_CELULAR_POR_CODIGO:
                    System.out.println("Servidor: Recebida requisição de consulta de celular por código.");
                    String codigoProdutoConsulta = ProtocoloEmpacotamento.desempacotarRequisicaoConsultaCelular(dis);
                    System.out.println("Servidor: Código do produto solicitado: " + codigoProdutoConsulta);
                    Produto produtoEncontrado = catalogoProdutos.get(codigoProdutoConsulta);
                    if (produtoEncontrado instanceof Celular) {
                        Celular celularEncontrado = (Celular) produtoEncontrado;
                        System.out.println("Servidor: Celular encontrado: " + celularEncontrado.getNome());
                        byte[] respostaBytes = ProtocoloEmpacotamento.empacotarRespostaCelular(celularEncontrado);
                        dos.write(respostaBytes);
                    } else if (produtoEncontrado != null) {
                        String msgErroProd = "Produto encontrado (código " + codigoProdutoConsulta + ") não é um celular. Use a opção de listar todos os produtos.";
                        System.out.println("Servidor: " + msgErroProd);
                        byte[] respostaErroBytesProd = ProtocoloEmpacotamento.empacotarMensagemErro(msgErroProd);
                        dos.write(respostaErroBytesProd);
                    } else {
                        String msgErroNaoEnc = "Produto com código " + codigoProdutoConsulta + " não encontrado.";
                        System.out.println("Servidor: " + msgErroNaoEnc);
                        byte[] respostaErroBytesNaoEnc = ProtocoloEmpacotamento.empacotarMensagemErro(msgErroNaoEnc);
                        dos.write(respostaErroBytesNaoEnc);
                    }
                    break;

                case ProtocoloEmpacotamento.OP_LISTAR_TODOS_CELULARES:
                    System.out.println("Servidor: Recebida requisição para listar todos os celulares.");
                    List<Celular> todosOsCelulares = catalogoProdutos.values().stream()
                                                     .filter(p -> p instanceof Celular)
                                                     .map(p -> (Celular) p)
                                                     .collect(Collectors.toList());
                    System.out.println("Servidor: Encontrados " + todosOsCelulares.size() + " celulares para listar.");
                    byte[] respostaBytesListaCel = ProtocoloEmpacotamento.empacotarRespostaListaCelulares(todosOsCelulares);
                    dos.write(respostaBytesListaCel);
                    break;

                case ProtocoloEmpacotamento.OP_REGISTRAR_VENDA:
                    System.out.println("Servidor: Recebida requisição para registrar venda.");
                    Object[] dadosVenda = ProtocoloEmpacotamento.desempacotarRequisicaoRegistrarVenda(dis);
                    String codigoProdutoVenda = (String) dadosVenda[0];
                    int quantidadeVenda = (Integer) dadosVenda[1];
                    String idVendedorVenda = (String) dadosVenda[2];
                    System.out.println("Servidor: Dados da venda - Produto: " + codigoProdutoVenda + ", Qtd: " + quantidadeVenda + ", Vendedor ID: " + idVendedorVenda);
                    Produto produtoParaVenda = catalogoProdutos.get(codigoProdutoVenda);
                    Vendedor vendedorDaVenda = catalogoVendedores.get(idVendedorVenda);
                    byte[] respostaVendaBytes;
                    if (produtoParaVenda == null) {
                        String msg = "Produto com código " + codigoProdutoVenda + " não encontrado.";
                        System.err.println("Servidor: " + msg);
                        respostaVendaBytes = ProtocoloEmpacotamento.empacotarRespostaVendaFalha(msg);
                    } else if (vendedorDaVenda == null) {
                        String msg = "Vendedor com ID " + idVendedorVenda + " não encontrado.";
                        System.err.println("Servidor: " + msg);
                        respostaVendaBytes = ProtocoloEmpacotamento.empacotarRespostaVendaFalha(msg);
                    } else {
                        boolean sucessoVenda = registroVendasServico.registrarVenda(produtoParaVenda, quantidadeVenda, vendedorDaVenda);
                        if (sucessoVenda) {
                            String msgSucesso = "Venda do produto '" + produtoParaVenda.getNome() + "' (Qtd: " + quantidadeVenda + ") registrada com sucesso!";
                            System.out.println("Servidor: " + msgSucesso);
                            gerenciadorEstoque.exibirStockAtual();
                            respostaVendaBytes = ProtocoloEmpacotamento.empacotarRespostaVendaSucesso(msgSucesso);
                        } else {
                            String msgFalha = "Falha ao registrar venda do produto '" + produtoParaVenda.getNome() + "'.";
                            System.err.println("Servidor: " + msgFalha + " (Verifique logs do RegistroVendasServico para detalhes como stock).");
                            respostaVendaBytes = ProtocoloEmpacotamento.empacotarRespostaVendaFalha(msgFalha + " Estoque pode ser insuficiente ou dados inválidos.");
                        }
                    }
                    dos.write(respostaVendaBytes);
                    break;

                case ProtocoloEmpacotamento.OP_LISTAR_TODOS_PRODUTOS: // NOVO CASE
                    System.out.println("Servidor: Recebida requisição para listar todos os produtos.");
                    List<Produto> todosOsProdutos = new ArrayList<>(catalogoProdutos.values());
                    System.out.println("Servidor: Encontrados " + todosOsProdutos.size() + " produtos no total para listar.");
                    byte[] respostaBytesTodosProd = ProtocoloEmpacotamento.empacotarRespostaListaTodosProdutos(todosOsProdutos);
                    dos.write(respostaBytesTodosProd);
                    break;

                default:
                    System.err.println("Servidor: Tipo de operação desconhecida recebida: " + tipoOperacao);
                    String msgErroOp = "Operação desconhecida: " + tipoOperacao;
                    byte[] respostaErroBytesOp = ProtocoloEmpacotamento.empacotarMensagemErro(msgErroOp);
                    dos.write(respostaErroBytesOp);
                    break;
            }
            dos.flush();
            return true;

        } catch (EOFException | SocketException e) {
            System.out.println("Servidor: Cliente desconectou (" + e.getClass().getSimpleName() + ") durante o processamento da operação " + tipoOperacao + ".");
            return false;
        } catch (IOException e) {
            System.err.println("Servidor: Erro de I/O ao processar operação " + tipoOperacao + ": " + e.getMessage());
            e.printStackTrace();
            try {
                byte[] respostaErroInterno = ProtocoloEmpacotamento.empacotarMensagemErro("Erro interno no servidor.");
                dos.write(respostaErroInterno);
                dos.flush();
            } catch (IOException ex) {
                System.err.println("Servidor: Não foi possível enviar mensagem de erro interna ao cliente: " + ex.getMessage());
            }
            return false;
        }
    }

    public static void main(String[] args) {
        int portaServidor = 54321;
        ServidorLojaTCP servidor = new ServidorLojaTCP(portaServidor);
        servidor.iniciar();
    }
}
