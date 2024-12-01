package Bean;

public class SessoesBean {
    private Integer idSessao;
    private int codigo;
    private String nome;

    public SessoesBean(Integer idSessao, int codigo, String nome) {
        this.idSessao = idSessao;
        this.codigo = codigo;
        this.nome = nome;
    }

    public SessoesBean(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public Integer getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(Integer idSessao) {
        this.idSessao = idSessao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "ID: " + idSessao + " : Código: " + codigo + " - Nome: " + nome;
    }
}
