package Bean;

public class PagamentosBean {
    private Integer idPagamento;
    private double valorPago;
    private double valorEmprestimo;
    private int status;
    private String statusString;
    private Integer idEmprestimo;

    public PagamentosBean(Integer idPagamento, double valorPago, int status, Integer idEmprestimo) {
        this.idPagamento = idPagamento;
        this.valorPago = valorPago;
        this.status = status;
        this.idEmprestimo = idEmprestimo;
    }
    
    public PagamentosBean(Integer idEmprestimo, double valorEmprestimo, double valorPago, String status) {
        this.idEmprestimo = idEmprestimo;
        this.valorEmprestimo = valorEmprestimo;
        this.valorPago = valorPago;
        this.statusString = status;
        this.idEmprestimo = idEmprestimo;
    }
    
    public PagamentosBean(double valorPago, int status, Integer idEmprestimo) {
        this.valorPago = valorPago;
        this.status = status;
        this.idEmprestimo = idEmprestimo;
    }
    
    public PagamentosBean(Integer idPagamento, double valorPago) {
        this.valorPago = valorPago;
        this.idPagamento = idPagamento;
    }

    public Integer getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Integer idPagamento) {
        this.idPagamento = idPagamento;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }
    
    public double getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(double valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public Integer getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Integer idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    @Override
    public String toString() {
        String statusDesc;
        switch (status) {
            case 1: statusDesc = "Pendente"; break;
            case 2: statusDesc = "Pago"; break;
            case 3: statusDesc = "Em Atraso"; break;
            default: statusDesc = "Pendente"; break;
        }
        return "Pagamento ID: " + idPagamento + ", Valor: R$ " + valorPago + ", Status: " + statusDesc + ", Empréstimo ID: " + idEmprestimo;
    }

    public String pagamentoEmprestimo() {
        return "ID Empréstimo: " + idEmprestimo +
                "\nValor Total: R$ " + String.format("%.2f", valorEmprestimo) +
                "\nValor Pago Total: R$ " + String.format("%.2f", valorPago) +
                "\nValor Devido: R$ " + String.format("%.2f", valorEmprestimo - valorPago) +
                "\nStatus : " + statusString + "\n";
    }
    
    public String listaPagamentos(){
        return"ID Pagamento: " + idPagamento + "\nValor: " + valorPago + "\n";
    }
}

