package Bean;
import java.util.Date;
import java.util.List;

public class EmprestimoDetalheBean {
    private Integer idEmprestimo;
    private double valorEmprestimo;
    private List<String> livros;
    private Date dataDevolucao;

    public EmprestimoDetalheBean(Integer idEmprestimo, double valorEmprestimo, List<String> livros, Date dataDevolucao) {
        this.idEmprestimo = idEmprestimo;
        this.valorEmprestimo = valorEmprestimo;
        this.livros = livros;
        this.dataDevolucao = dataDevolucao;
    }

    public Integer getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Integer idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public double getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(double valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public List<String> getLivros() {
        return livros;
    }

    public void setLivros(List<String> livros) {
        this.livros = livros;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID do Empréstimo: ").append(idEmprestimo).append("\n");
        sb.append("Valor do Empréstimo: ").append(valorEmprestimo).append("\n");
        sb.append("Data de devolução: ").append(dataDevolucao).append("\n");
        sb.append("Livros:\n");
        for (String livro : livros) {
            sb.append(" - ").append(livro).append(" \n");
        }
        return sb.toString();
    }
}
