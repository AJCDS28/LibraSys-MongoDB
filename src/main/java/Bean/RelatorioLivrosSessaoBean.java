package Bean;
import java.util.List;

public class RelatorioLivrosSessaoBean {
    private Integer idSessao;
    private int codigo;
    private String nomeSessao;
    private List<String> titulosLivros;

    public RelatorioLivrosSessaoBean(Integer idSessao, int codigo, String nomeSessao, List<String> titulosLivros) {
        this.idSessao = idSessao;
        this.codigo = codigo;
        this.nomeSessao = nomeSessao;
        this.titulosLivros = titulosLivros;
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

    public String getNomeSessao() {
        return nomeSessao;
    }

    public void setNomeSessao(String nomeSessao) {
        this.nomeSessao = nomeSessao;
    }

    public List<String> getTitulosLivros() {
        return titulosLivros;
    }

    public void setTitulosLivros(List<String> titulosLivros) {
        this.titulosLivros = titulosLivros;
    }

    @Override
    public String toString() {
        String livros = (titulosLivros.isEmpty()) ? "Sem livros associados" : String.join(", ", titulosLivros);
        return "Sessão: " + codigo + " - " + nomeSessao + "\nLivros: " + livros + "\n";
    }
}
