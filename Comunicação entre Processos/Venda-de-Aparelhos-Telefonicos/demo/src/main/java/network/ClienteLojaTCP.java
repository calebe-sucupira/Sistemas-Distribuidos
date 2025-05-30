package network;

import entidades.Capa;
import entidades.Celular;
import entidades.Pelicula;
import entidades.PowerBank;
import entidades.Produto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class ClienteLojaTCP {

    private String host;
    private int porta;

    public ClienteLojaTCP(String host, int porta) {
        this.host = host;
        this.porta = porta;
    }

    public void iniciar() {
        try (Socket socket = new Socket(host, porta);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Cliente conectado ao servidor em " + host + ":" + porta);

            while (true) {
                System.out.println("\nEscolha uma operação:");
                System.out.println("1. Consultar celular por código");
                System.out.println("2. Listar todos os celulares (apenas celulares)");
                System.out.println("3. Registrar venda");
                System.out.println("4. Listar todos os produtos da loja");
                System.out.println("0. Sair");
                System.out.print("Opção: ");
                String escolha = scanner.nextLine();

                if ("0".equals(escolha)) {
                    System.out.println("Cliente encerrando.");
                    break;
                }

                try {
                    switch (escolha) {
                        case "1":
                            System.out.print("Digite o código do produto para consulta (ex: SAM001): ");
                            String codigoParaConsultar = scanner.nextLine();
                            if (codigoParaConsultar.trim().isEmpty()) {
                                System.out.println("Código não pode ser vazio.");
                                continue;
                            }
                            System.out.println("Cliente: Enviando requisição de consulta para o código: " + codigoParaConsultar);
                            byte[] requisicaoConsultaBytes = ProtocoloEmpacotamento.empacotarRequisicaoConsultaCelular(codigoParaConsultar);
                            dos.write(requisicaoConsultaBytes);
                            dos.flush();
                            lerEProcessarRespostaServidor(dis);
                            break;

                        case "2":
                            System.out.println("Cliente: Enviando requisição para listar todos os celulares.");
                            byte[] requisicaoListarCelBytes = ProtocoloEmpacotamento.empacotarRequisicaoListarTodosCelulares();
                            dos.write(requisicaoListarCelBytes);
                            dos.flush();
                            lerEProcessarRespostaServidor(dis);
                            break;

                        case "3":
                            System.out.println("Cliente: Registrar nova venda.");
                            System.out.print("  Digite o código do produto: ");
                            String codProdVenda = scanner.nextLine();
                            System.out.print("  Digite a quantidade: ");
                            int qtdVenda;
                            try {
                                qtdVenda = Integer.parseInt(scanner.nextLine());
                                if (qtdVenda <= 0) {
                                    System.out.println("Quantidade deve ser positiva.");
                                    continue;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Quantidade inválida. Deve ser um número.");
                                continue;
                            }
                            System.out.print("  Digite o ID do vendedor (ex: V101, V102): ");
                            String idVendVenda = scanner.nextLine();
                            if (codProdVenda.trim().isEmpty() || idVendVenda.trim().isEmpty()) {
                                System.out.println("Código do produto e ID do vendedor não podem ser vazios.");
                                continue;
                            }
                            System.out.println("Cliente: Enviando requisição para registrar venda...");
                            byte[] requisicaoVendaBytes = ProtocoloEmpacotamento.empacotarRequisicaoRegistrarVenda(codProdVenda, qtdVenda, idVendVenda);
                            dos.write(requisicaoVendaBytes);
                            dos.flush();
                            lerEProcessarRespostaServidor(dis);
                            break;

                        case "4":
                            System.out.println("Cliente: Enviando requisição para listar todos os produtos da loja.");
                            byte[] requisicaoListarTodosProdBytes = ProtocoloEmpacotamento.empacotarRequisicaoListarTodosProdutos();
                            dos.write(requisicaoListarTodosProdBytes);
                            dos.flush();
                            lerEProcessarRespostaServidor(dis);
                            break;

                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                            break;
                    }
                } catch (SocketException | EOFException se) {
                    System.err.println("Cliente: Conexão com o servidor perdida ou fechada: " + se.getMessage());
                    return;
                } catch (IOException e) {
                    System.err.println("Cliente: Erro de comunicação durante a operação: " + e.getMessage());
                    return;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Erro: Host desconhecido - " + host + ":" + porta);
        } catch (SocketException se ) {
            System.err.println("Erro ao conectar ao servidor (SocketException): " + se.getMessage() + ". Verifique se o servidor está em execução.");
        } catch (IOException e) {
            System.err.println("Erro de I/O ao conectar ou na comunicação inicial com o servidor: " + e.getMessage());
        }
        System.out.println("Cliente: Conexão finalizada.");
    }

    private void lerEProcessarRespostaServidor(DataInputStream dis) throws IOException {
        System.out.println("Cliente: Aguardando resposta do servidor...");
        byte tipoResposta = dis.readByte();
        System.out.println("Cliente: Tipo de Resposta Recebida: " + tipoResposta);

        if (tipoResposta == ProtocoloEmpacotamento.OP_RESPOSTA_CELULAR_DETALHES) {
            Celular celularRecebido = ProtocoloEmpacotamento.desempacotarRespostaCelular(dis);
            System.out.println("Cliente: Detalhes do Celular Recebido:");
            imprimirDetalhesCelular(celularRecebido);
        } else if (tipoResposta == ProtocoloEmpacotamento.OP_RESPOSTA_LISTA_CELULARES) {
            List<Celular> celularesRecebidos = ProtocoloEmpacotamento.desempacotarRespostaListaCelulares(dis);
            System.out.println("Cliente: Lista de Celulares Recebida (" + celularesRecebidos.size() + " itens):");
            if (celularesRecebidos.isEmpty()) {
                System.out.println("  Nenhum celular retornado pelo servidor.");
            } else {
                for (Celular celular : celularesRecebidos) {
                    System.out.println("  --- [CELULAR] ---");
                    imprimirDetalhesCelular(celular);
                }
            }
        } else if (tipoResposta == ProtocoloEmpacotamento.OP_RESPOSTA_VENDA_SUCESSO) {
            String msgSucesso = ProtocoloEmpacotamento.desempacotarRespostaVendaSucesso(dis);
            System.out.println("Cliente: Resposta do Servidor (Venda Sucesso): " + msgSucesso);
        } else if (tipoResposta == ProtocoloEmpacotamento.OP_RESPOSTA_VENDA_FALHA) {
            String msgFalha = ProtocoloEmpacotamento.desempacotarRespostaVendaFalha(dis);
            System.err.println("Cliente: Resposta do Servidor (Venda Falhou): " + msgFalha);
        } else if (tipoResposta == ProtocoloEmpacotamento.OP_RESPOSTA_LISTA_PRODUTOS) { 
            List<Produto> produtosRecebidos = ProtocoloEmpacotamento.desempacotarRespostaListaTodosProdutos(dis);
            System.out.println("Cliente: Lista de Todos os Produtos Recebida (" + produtosRecebidos.size() + " itens):");
            if (produtosRecebidos.isEmpty()) {
                System.out.println("  Nenhum produto retornado pelo servidor.");
            } else {
                for (Produto produto : produtosRecebidos) {
                    System.out.println("  ---");
                    imprimirDetalhesProduto(produto);
                }
            }
        } else if (tipoResposta == ProtocoloEmpacotamento.OP_ERRO) {
            String mensagemErro = ProtocoloEmpacotamento.desempacotarMensagemErro(dis);
            System.err.println("Cliente: Erro recebido do servidor: " + mensagemErro);
        } else {
            System.err.println("Cliente: Tipo de resposta desconhecida (" + tipoResposta + ") recebida do servidor.");
        }
    }

    private void imprimirDetalhesCelular(Celular celular) {
        if (celular == null) {
            System.out.println("    Dados do celular nulos.");
            return;
        }
        System.out.println("    Tipo: Celular");
        System.out.println("    Código: " + celular.getCodigo());
        System.out.println("    Nome: " + celular.getNome());
        System.out.println("    Marca: " + celular.getMarca());
        System.out.println("    Preço: R$" + String.format("%.2f", celular.getPreco()));
        System.out.println("    Modelo: " + celular.getModelo());
        System.out.println("    Cor: " + celular.getColor());
        System.out.println("    Memória Interna: " + celular.getMemoriaInterna());
        System.out.println("    Memória RAM: " + celular.getMemoriaRam());
    }

    private void imprimirDetalhesProduto(Produto produto) {
        if (produto == null) {
            System.out.println("    Dados do produto nulos.");
            return;
        }
        System.out.println("    Código: " + produto.getCodigo());
        System.out.println("    Nome: " + produto.getNome());
        System.out.println("    Marca: " + produto.getMarca());
        System.out.println("    Preço: R$" + String.format("%.2f", produto.getPreco()));

        if (produto instanceof Celular) {
            Celular cel = (Celular) produto;
            System.out.println("    Tipo: Celular");
            System.out.println("    Modelo: " + cel.getModelo());
            System.out.println("    Cor: " + cel.getColor());
            System.out.println("    Memória Interna: " + cel.getMemoriaInterna());
            System.out.println("    Memória RAM: " + cel.getMemoriaRam());
        } else if (produto instanceof Capa) {
            Capa c = (Capa) produto;
            System.out.println("    Tipo: Capa");
            System.out.println("    Material: " + c.getTipoMaterial());
            System.out.println("    Cor: " + c.getCor());
            System.out.println("    Compatibilidade: " + c.getCompatibilidadeModeloCelular());
            if (c.getCaracteristicasAdicionais() != null && !c.getCaracteristicasAdicionais().isEmpty()) {
                System.out.println("    Características: " + c.getCaracteristicasAdicionais());
            }
        } else if (produto instanceof Pelicula) {
            Pelicula p = (Pelicula) produto;
            System.out.println("    Tipo: Película");
            System.out.println("    Material: " + p.getTipoMaterial());
            System.out.println("    Compatibilidade: " + p.getCompatibilidadeModeloCelular());
        } else if (produto instanceof PowerBank) {
            PowerBank pb = (PowerBank) produto;
            System.out.println("    Tipo: PowerBank");
            System.out.println("    Capacidade: " + pb.getCapacidadeMah() + "mAh");
            System.out.println("    Portas USB: " + pb.getNumeroPortasUSB());
            System.out.println("    Tipos de Porta: " + pb.getTipoPortas());
            if (pb.getTecnologiaCarregamentoRapido() != null && !pb.getTecnologiaCarregamentoRapido().isEmpty()) {
                System.out.println("    Carregamento Rápido: " + pb.getTecnologiaCarregamentoRapido());
            }
        } else {
            System.out.println("    Tipo: Produto Genérico");
        }
    }

    public static void main(String[] args) {
        ClienteLojaTCP cliente = new ClienteLojaTCP("localhost", 54321);
        cliente.iniciar();
    }
}
