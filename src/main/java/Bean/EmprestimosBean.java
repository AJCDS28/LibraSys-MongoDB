package Bean;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class EmprestimosBean {
    private Integer idEmprestimo;
    private Integer idCliente;
    private Date dataEmprestimo;
    private Date dataDevolucao;
    private String status;
    private int statusEmp;
    private double valorEmprestimo;
    private String statusPagamento;
    private String tituloLivro;
    private String nomeCliente;
    private List<String> titulosLivros;

    public EmprestimosBean(Integer idCliente, Date dataEmprestimo, Date dataDevolucao, int statusEmp, double valorEmprestimo) {
        this.idCliente = idCliente;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.statusEmp = statusEmp;
        this.valorEmprestimo = valorEmprestimo;
    }

    public EmprestimosBean(Integer idEmprestimo, Integer idCliente, Date dataEmprestimo, Date dataDevolucao, int statusEmp, double valorEmprestimo) {
        this.idEmprestimo = idEmprestimo;
        this.idCliente = idCliente;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.statusEmp = statusEmp;
        this.valorEmprestimo = valorEmprestimo;
    }
    
    public EmprestimosBean(Integer idEmprestimo,String nomeCliente, Date dataEmprestimo, Date dataDevolucao, String status, double valorEmprestimo, String statusPagamento, String tituloLivro) {
        this.idEmprestimo = idEmprestimo;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
        this.valorEmprestimo = valorEmprestimo;
        this.tituloLivro = tituloLivro;
        this.statusPagamento = statusPagamento;
        this.nomeCliente = nomeCliente;
    }
    
    public EmprestimosBean(Integer idEmprestimo,Date dataEmprestimo, Date dataDevolucao, String status, double valorEmprestimo, String statusPagamento, String tituloLivro, String nomeCliente) {
        this.idEmprestimo = idEmprestimo;
        this.nomeCliente = nomeCliente;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
        this.valorEmprestimo = valorEmprestimo;
        this.statusPagamento = statusPagamento;
        this.nomeCliente = nomeCliente;
        this.titulosLivros = new ArrayList<>();
        this.titulosLivros.add(tituloLivro);
    }

    public Integer getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Integer idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Date getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(Date dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getStatusEmp() {
        return statusEmp;
    }

    public void setStatusEmp(int statusEmp) {
        this.statusEmp = statusEmp;
    }
    
    public double getvalorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(double valoremprestimo) {
        this.valorEmprestimo = valoremprestimo;
    }
    
    public String getTituloLivro() {
        return tituloLivro;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }
    
    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
    
    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
    
    public void addTituloLivro(String tituloLivro) {
        if (!titulosLivros.contains(tituloLivro)) {
            titulosLivros.add(tituloLivro);
        }
    }

    @Override
    public String toString() {
        return "Empréstimo ID: " + idEmprestimo 
               + " - Cliente: " + nomeCliente 
               + " - Data de Empréstimo: " + dataEmprestimo 
               + " - Data de Devolução: " + dataDevolucao 
               + " - Valor do Empréstimo: " + valorEmprestimo 
               + " - Status do Empréstimo: " + status 
               + " - Status do Pagamento: " + statusPagamento 
               + " - Títulos dos Livros: " + String.join(", ", titulosLivros);
    }
}
