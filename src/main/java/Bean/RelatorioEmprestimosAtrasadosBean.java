package Bean;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioEmprestimosAtrasadosBean {
   private String nomeCliente;
    private Map<Integer, EmprestimoDetalheBean> emprestimosAtrasados;

    public RelatorioEmprestimosAtrasadosBean(String nomeCliente) {
        this.nomeCliente = nomeCliente;
        this.emprestimosAtrasados = new HashMap<>();
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Map<Integer, EmprestimoDetalheBean> getEmprestimosAtrasados() {
        return emprestimosAtrasados;
    }

    public void setEmprestimosAtrasados(Map<Integer, EmprestimoDetalheBean> emprestimosAtrasados) {
        this.emprestimosAtrasados = emprestimosAtrasados;
    }

    public void addEmprestimo(Integer idEmprestimo, double valorEmprestimo, String tituloLivro, Date dataDevolucao) {
        EmprestimoDetalheBean emprestimoDetalhe = emprestimosAtrasados.get(idEmprestimo);

        if (emprestimoDetalhe == null) {
            List<String> livros = new ArrayList<>();
            livros.add(tituloLivro);
            emprestimoDetalhe = new EmprestimoDetalheBean(idEmprestimo, valorEmprestimo, livros, dataDevolucao);
            emprestimosAtrasados.put(idEmprestimo, emprestimoDetalhe);
        } else {
            List<String> livros = emprestimoDetalhe.getLivros();
            if (!livros.contains(tituloLivro)) {
                livros.add(tituloLivro);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome do Cliente: ").append(nomeCliente).append("\n");
        sb.append("Empréstimos Atrasados:\n");

        for (EmprestimoDetalheBean emprestimo : emprestimosAtrasados.values()) {
            sb.append(emprestimo.toString()).append("\n");
        }

        return sb.toString();
    }
}


