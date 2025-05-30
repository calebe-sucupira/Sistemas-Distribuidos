package network;

import entidades.Capa;
import entidades.Celular;
import entidades.Pelicula;
import entidades.PowerBank;
import entidades.Produto;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProtocoloEmpacotamento {

    public static final byte OP_CONSULTAR_CELULAR_POR_CODIGO = 1;
    public static final byte OP_RESPOSTA_CELULAR_DETALHES = 2;
    public static final byte OP_LISTAR_TODOS_CELULARES = 3;
    public static final byte OP_RESPOSTA_LISTA_CELULARES = 4;
    public static final byte OP_REGISTRAR_VENDA = 5;
    public static final byte OP_RESPOSTA_VENDA_SUCESSO = 6;
    public static final byte OP_RESPOSTA_VENDA_FALHA = 7;
    public static final byte OP_LISTAR_TODOS_PRODUTOS = 8; 
    public static final byte OP_RESPOSTA_LISTA_PRODUTOS = 9;
    public static final byte OP_ERRO = -1; 

    public static final byte TIPO_PRODUTO_GENERICO = 0;
    public static final byte TIPO_PRODUTO_CELULAR = 1;
    public static final byte TIPO_PRODUTO_CAPA = 2;
    public static final byte TIPO_PRODUTO_PELICULA = 3;
    public static final byte TIPO_PRODUTO_POWERBANK = 4;

    public static byte[] empacotarRequisicaoConsultaCelular(String codigoProduto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_CONSULTAR_CELULAR_POR_CODIGO);
        dos.writeUTF(codigoProduto != null ? codigoProduto : "");
        dos.close();
        return baos.toByteArray();
    }

    public static String desempacotarRequisicaoConsultaCelular(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }

    private static void empacotarAtributosCelular(DataOutputStream dos, Celular celular) throws IOException {
        dos.writeUTF(celular.getModelo() != null ? celular.getModelo() : "N/A");
        dos.writeUTF(celular.getColor() != null ? celular.getColor() : "N/A");
        dos.writeUTF(celular.getMemoriaInterna() != null ? celular.getMemoriaInterna() : "0GB");
        dos.writeUTF(celular.getMemoriaRam() != null ? celular.getMemoriaRam() : "0GB");
    }

    private static void desempacotarAtributosCelular(DataInputStream dis, Celular celular) throws IOException {
        celular.setModelo(dis.readUTF());
        celular.setColor(dis.readUTF());
        celular.setMemoriaInterna(dis.readUTF());
        celular.setMemoriaRam(dis.readUTF());
    }

    private static void empacotarAtributosCapa(DataOutputStream dos, Capa capa) throws IOException {
        dos.writeUTF(capa.getTipoMaterial() != null ? capa.getTipoMaterial() : "N/A");
        dos.writeUTF(capa.getCor() != null ? capa.getCor() : "N/A");
        dos.writeUTF(capa.getCompatibilidadeModeloCelular() != null ? capa.getCompatibilidadeModeloCelular() : "N/A");
        dos.writeUTF(capa.getCaracteristicasAdicionais() != null ? capa.getCaracteristicasAdicionais() : "");
    }

    private static void desempacotarAtributosCapa(DataInputStream dis, Capa capa) throws IOException {
        capa.setTipoMaterial(dis.readUTF());
        capa.setCor(dis.readUTF());
        capa.setCompatibilidadeModeloCelular(dis.readUTF());
        capa.setCaracteristicasAdicionais(dis.readUTF());
    }

    private static void empacotarAtributosPelicula(DataOutputStream dos, Pelicula pelicula) throws IOException {
        dos.writeUTF(pelicula.getTipoMaterial() != null ? pelicula.getTipoMaterial() : "N/A");
        dos.writeUTF(pelicula.getCompatibilidadeModeloCelular() != null ? pelicula.getCompatibilidadeModeloCelular() : "N/A");
    }

    private static void desempacotarAtributosPelicula(DataInputStream dis, Pelicula pelicula) throws IOException {
        pelicula.setTipoMaterial(dis.readUTF());
        pelicula.setCompatibilidadeModeloCelular(dis.readUTF());
    }

    private static void empacotarAtributosPowerBank(DataOutputStream dos, PowerBank powerBank) throws IOException {
        dos.writeInt(powerBank.getCapacidadeMah());
        dos.writeInt(powerBank.getNumeroPortasUSB());
        dos.writeUTF(powerBank.getTipoPortas() != null ? powerBank.getTipoPortas() : "N/A");
        dos.writeUTF(powerBank.getTecnologiaCarregamentoRapido() != null ? powerBank.getTecnologiaCarregamentoRapido() : "");
    }

    private static void desempacotarAtributosPowerBank(DataInputStream dis, PowerBank powerBank) throws IOException {
        powerBank.setCapacidadeMah(dis.readInt());
        powerBank.setNumeroPortasUSB(dis.readInt());
        powerBank.setTipoPortas(dis.readUTF());
        powerBank.setTecnologiaCarregamentoRapido(dis.readUTF());
    }

    private static void empacotarCelularCompleto(DataOutputStream dos, Celular celular) throws IOException {
        dos.writeUTF(celular.getCodigo() != null ? celular.getCodigo() : "N/A");
        dos.writeUTF(celular.getNome() != null ? celular.getNome() : "N/A");
        dos.writeUTF(celular.getMarca() != null ? celular.getMarca() : "N/A");
        dos.writeDouble(celular.getPreco());
        empacotarAtributosCelular(dos, celular);
    }

    private static Celular desempacotarCelularCompleto(DataInputStream dis) throws IOException {
        String codigo = dis.readUTF();
        String nome = dis.readUTF();
        String marca = dis.readUTF();
        double preco = dis.readDouble();
        Celular celular = new Celular(codigo, nome, marca, preco, "", "", "", "");
        desempacotarAtributosCelular(dis, celular);
        return celular;
    }

    public static byte[] empacotarRespostaCelular(Celular celular) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_RESPOSTA_CELULAR_DETALHES);
        empacotarCelularCompleto(dos, celular);
        dos.close();
        return baos.toByteArray();
    }

    public static Celular desempacotarRespostaCelular(DataInputStream dis) throws IOException {
        return desempacotarCelularCompleto(dis);
    }

    public static byte[] empacotarRequisicaoListarTodosCelulares() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_LISTAR_TODOS_CELULARES);
        dos.close();
        return baos.toByteArray();
    }

    public static byte[] empacotarRespostaListaCelulares(List<Celular> celulares) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_RESPOSTA_LISTA_CELULARES);
        if (celulares == null) {
            dos.writeInt(0);
        } else {
            dos.writeInt(celulares.size());
            for (Celular celular : celulares) {
                empacotarCelularCompleto(dos, celular);
            }
        }
        dos.close();
        return baos.toByteArray();
    }

    public static List<Celular> desempacotarRespostaListaCelulares(DataInputStream dis) throws IOException {
        List<Celular> listaRecebida = new ArrayList<>();
        int numeroDeCelulares = dis.readInt();
        for (int i = 0; i < numeroDeCelulares; i++) {
            listaRecebida.add(desempacotarCelularCompleto(dis));
        }
        return listaRecebida;
    }

    public static byte[] empacotarRequisicaoRegistrarVenda(String codigoProduto, int quantidade, String idVendedor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_REGISTRAR_VENDA);
        dos.writeUTF(codigoProduto != null ? codigoProduto : "");
        dos.writeInt(quantidade);
        dos.writeUTF(idVendedor != null ? idVendedor : "");
        dos.close();
        return baos.toByteArray();
    }

    public static Object[] desempacotarRequisicaoRegistrarVenda(DataInputStream dis) throws IOException {
        String codigoProduto = dis.readUTF();
        int quantidade = dis.readInt();
        String idVendedor = dis.readUTF();
        return new Object[]{codigoProduto, quantidade, idVendedor};
    }

    public static byte[] empacotarRespostaVendaSucesso(String mensagemSucesso) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_RESPOSTA_VENDA_SUCESSO);
        dos.writeUTF(mensagemSucesso != null ? mensagemSucesso : "Venda registrada com sucesso!");
        dos.close();
        return baos.toByteArray();
    }

    public static String desempacotarRespostaVendaSucesso(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }

    public static byte[] empacotarRespostaVendaFalha(String mensagemFalha) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_RESPOSTA_VENDA_FALHA);
        dos.writeUTF(mensagemFalha != null ? mensagemFalha : "Falha ao registrar venda.");
        dos.close();
        return baos.toByteArray();
    }

    public static String desempacotarRespostaVendaFalha(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }

    public static byte[] empacotarRequisicaoListarTodosProdutos() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_LISTAR_TODOS_PRODUTOS);
        dos.close();
        return baos.toByteArray();
    }

    private static void empacotarProdutoDaLista(DataOutputStream dos, Produto produto) throws IOException {
        byte tipoProdutoByte;
        if (produto instanceof Celular) {
            tipoProdutoByte = TIPO_PRODUTO_CELULAR;
        } else if (produto instanceof Capa) {
            tipoProdutoByte = TIPO_PRODUTO_CAPA;
        } else if (produto instanceof Pelicula) {
            tipoProdutoByte = TIPO_PRODUTO_PELICULA;
        } else if (produto instanceof PowerBank) {
            tipoProdutoByte = TIPO_PRODUTO_POWERBANK;
        } else {
            tipoProdutoByte = TIPO_PRODUTO_GENERICO;
        }
        dos.writeByte(tipoProdutoByte);

        dos.writeUTF(produto.getCodigo() != null ? produto.getCodigo() : "N/A");
        dos.writeUTF(produto.getNome() != null ? produto.getNome() : "N/A");
        dos.writeUTF(produto.getMarca() != null ? produto.getMarca() : "N/A");
        dos.writeDouble(produto.getPreco());

        switch (tipoProdutoByte) {
            case TIPO_PRODUTO_CELULAR:
                empacotarAtributosCelular(dos, (Celular) produto);
                break;
            case TIPO_PRODUTO_CAPA:
                empacotarAtributosCapa(dos, (Capa) produto);
                break;
            case TIPO_PRODUTO_PELICULA:
                empacotarAtributosPelicula(dos, (Pelicula) produto);
                break;
            case TIPO_PRODUTO_POWERBANK:
                empacotarAtributosPowerBank(dos, (PowerBank) produto);
                break;
        }
    }

    public static byte[] empacotarRespostaListaTodosProdutos(List<Produto> produtos) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_RESPOSTA_LISTA_PRODUTOS);
        if (produtos == null) {
            dos.writeInt(0);
        } else {
            dos.writeInt(produtos.size());
            for (Produto produto : produtos) {
                empacotarProdutoDaLista(dos, produto);
            }
        }
        dos.close();
        return baos.toByteArray();
    }

    private static Produto desempacotarProdutoDaLista(DataInputStream dis) throws IOException {
        byte tipoProduto = dis.readByte();

        String codigo = dis.readUTF();
        String nome = dis.readUTF();
        String marca = dis.readUTF();
        double preco = dis.readDouble();

        Produto produto;
        switch (tipoProduto) {
            case TIPO_PRODUTO_CELULAR:
                Celular celular = new Celular(codigo, nome, marca, preco, "", "", "", "");
                desempacotarAtributosCelular(dis, celular);
                produto = celular;
                break;
            case TIPO_PRODUTO_CAPA:
                Capa capa = new Capa(codigo, nome, marca, preco, "", "", "", "");
                desempacotarAtributosCapa(dis, capa);
                produto = capa;
                break;
            case TIPO_PRODUTO_PELICULA:
                Pelicula pelicula = new Pelicula(codigo, nome, marca, preco, "", "", "");
                desempacotarAtributosPelicula(dis, pelicula);
                produto = pelicula;
                break;
            case TIPO_PRODUTO_POWERBANK:
                PowerBank powerBank = new PowerBank(codigo, nome, marca, preco, 0, 0, "", "");
                desempacotarAtributosPowerBank(dis, powerBank);
                produto = powerBank;
                break;
            case TIPO_PRODUTO_GENERICO:
            default:
                produto = new Produto(codigo, nome, marca, preco);
                break;
        }
        return produto;
    }

    public static List<Produto> desempacotarRespostaListaTodosProdutos(DataInputStream dis) throws IOException {
        List<Produto> listaRecebida = new ArrayList<>();
        int numeroDeProdutos = dis.readInt();
        for (int i = 0; i < numeroDeProdutos; i++) {
            listaRecebida.add(desempacotarProdutoDaLista(dis));
        }
        return listaRecebida;
    }

    public static byte[] empacotarMensagemErro(String mensagemErro) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(OP_ERRO);
        dos.writeUTF(mensagemErro != null ? mensagemErro : "Erro desconhecido.");
        dos.close();
        return baos.toByteArray();
    }

    public static String desempacotarMensagemErro(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }
}
