package entidades;


public class Vendedor {
    private String idVendedor;
    private String nomeCompleto;
    private String cpf;
    private String dataAdmissao; 

    public Vendedor(String idVendedor, String nomeCompleto, String cpf, String dataAdmissao) {
        this.idVendedor = idVendedor;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.dataAdmissao = dataAdmissao;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    @Override
    public String toString() {
        return "Vendedor [" +
               "idVendedor='" + idVendedor + '\'' +
               ", nomeCompleto='" + nomeCompleto + '\'' +
               ", cpf='" + cpf + '\'' +
               ", dataAdmissao=" + dataAdmissao + 
               "]";
    }
}