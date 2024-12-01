package Controller;

import Bean.PagamentosBean;
import Model.EmprestimosModel;
import Model.PagamentosModel;
import java.util.LinkedHashSet;
import Main.EntradaSaida;
import com.mongodb.client.MongoDatabase;

public class PagamentosController {
    private final PagamentosModel pagamentosModel = new PagamentosModel();
    private final EmprestimosModel emprestimosModel = new EmprestimosModel();
    
    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuPagamentos();
        switch (opt) {
            case 1 -> processarPagamentoNaDevolucao(database);
            case 2 -> {
                String pagamentosListados = listarPagamentos(database);
                if (pagamentosListados.isEmpty()) {
                    EntradaSaida.showMessage("Não há pagamentos cadastrados.\n");
                    menu(database);
                } else {
                    EntradaSaida.showMessage(pagamentosListados);
                    menu(database);
                }
            }
            case 3 -> excluirPagamento(database);
        }
    }

    public boolean createPagamento(PagamentosBean pagamento, MongoDatabase database) {
        try {
            return PagamentosModel.createPagamento(pagamento, database);
        } catch (Exception e) {
            System.err.println("Erro ao criar pagamento: " + e.getMessage());
            return false;
        }
    }
    
    public void processarPagamentoNaDevolucao(MongoDatabase database) {
        EmprestimosController emp = new EmprestimosController();

        if (emp.listarEmprestimosNãoQuitados(database).equals("")) {
            EntradaSaida.showMessage("Não há empréstimos sem quitação.");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(emp.listarEmprestimosNãoQuitados(database));

        Integer idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo:");
        
        if (!EmprestimosModel.emprestimoExiste(database,idEmprestimo)) {
            EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
            menu(database);
            return;
        }

        double valorEmprestimo = emprestimosModel.getValorEmprestimo(idEmprestimo, database);
        double valorPagoTotal = pagamentosModel.getValorPagoTotal(idEmprestimo, database);
        double valorDevido = valorEmprestimo - valorPagoTotal;
        valorDevido = Math.round(valorDevido * 100.0) / 100.0;

        if (valorDevido <= 0) {
            EntradaSaida.showMessage("O empréstimo já está totalmente pago.");
            menu(database);
            return;
        }

        EntradaSaida.showMessage("O valor total devido é R$ " + String.format("%.2f", valorDevido));

        while (valorDevido > 0) {
            double valorPago = EntradaSaida.getDecimal("Digite o valor exato a ser pago:");

            if (valorPago < valorDevido) {
                EntradaSaida.showMessage("O valor pago é menor que o valor devido.");
            } else if (valorPago > valorDevido) {
                EntradaSaida.showMessage("O valor pago é maior que o valor devido.");
            } else {
                PagamentosBean pagamentoExistente = PagamentosModel.getPagamentoPorEmprestimo(idEmprestimo, database);

                if (pagamentoExistente != null) {
                    boolean sucessoUpdate = PagamentosModel.atualizarPagamento(idEmprestimo, valorPago, database);

                    if (sucessoUpdate) {
                        EntradaSaida.showMessage("Pagamento atualizado com sucesso. Empréstimo totalmente quitado.");
                        valorDevido = 0;
                    } else {
                        EntradaSaida.showMessage("Erro ao atualizar o pagamento.");
                        break;
                    }
                } else {
                    PagamentosBean novoPagamento = new PagamentosBean(valorPago, 2, idEmprestimo);
                    boolean sucessoInsert = PagamentosModel.createPagamento(novoPagamento, database);

                    if (sucessoInsert) {
                        EntradaSaida.showMessage("Pagamento registrado com sucesso. Empréstimo totalmente quitado.");
                        valorDevido = 0;
                    } else {
                        EntradaSaida.showMessage("Erro ao registrar o pagamento.");
                        break;
                    }
                }
            }
        }
        menu(database);
    }


    public void excluirPagamento(MongoDatabase database) {
        EmprestimosController emprestimos = new EmprestimosController();
        if(emprestimos.listarEmprestimos(database).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(database);
            return;
        }
       EntradaSaida.showMessage(emprestimos.listarEmprestimos(database));
       
        Integer idEmprestimo = EntradaSaida.getNumber("Digite o id do emprestimo: ");
        
        if (!EmprestimosModel.emprestimoExiste(database,idEmprestimo)) {
            EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
            menu(database);
            return;
        }
       
        if (EmprestimosModel.isEmprestimoDevolvido(idEmprestimo, database)) {
            EntradaSaida.showMessage("Emprestimo devolvido, não é possivel excluir o pagamento");
            menu(database);
            return;
        }
        if (listarPagamentosExclusao(database).equals("")) {
            EntradaSaida.showMessage("Não há pagamentos cadastrados");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(listarPagamentosExclusao(database));

        Integer idPagamento = EntradaSaida.getNumber("Digite o ID do pagamento que deseja excluir: ");

        if (!PagamentosModel.existePagamento(idPagamento, database )) {
            EntradaSaida.showMessage("Pagamento não encontrado.");
            menu(database);
            return;
        }

        int confirmacao = EntradaSaida.getNumber("Tem certeza que deseja excluir o pagamento? (1 - Sim, 2 - Não)");

        if (confirmacao == 1) {
            if (PagamentosModel.excluirPagamento(idPagamento, database)) {
                EntradaSaida.showMessage("Pagamento excluído com sucesso.");
            } else {
                EntradaSaida.showMessage("Erro ao excluir pagamento.");
            }
        } else {
            EntradaSaida.showMessage("Exclusão cancelada.");
        }
        menu(database);
    }

    
    public String listarPagamentosExclusao(MongoDatabase database) {
        LinkedHashSet<PagamentosBean> pagamentos = PagamentosModel.listarPagamentos(database);
        StringBuilder sb = new StringBuilder();

        if (pagamentos.isEmpty()) {
            sb.append("");
        } else {
            sb.append("Lista de Pagamentos:\n");
            for (PagamentosBean pagamento : pagamentos) {
                sb.append(pagamento.listaPagamentos()).append("\n");
            }
        }

        return sb.toString();
    }

    
    public String listarPagamentos(MongoDatabase database) {
        EmprestimosController emp = new EmprestimosController();

        if (emp.listarEmprestimos(database).equals("")) {
            return "Não há empréstimos cadastrados";
        }
        EntradaSaida.showMessage(emp.listarEmprestimos(database));

        Integer idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo: ");
        
        if (!EmprestimosModel.emprestimoExiste(database,idEmprestimo)) {
            EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
            menu(database);
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try {
            LinkedHashSet<PagamentosBean> pagamentos = PagamentosModel.listarPagamentos(idEmprestimo, database);
            sb.append("Lista de Pagamentos:\n\n");
            for (PagamentosBean pagamento : pagamentos) {
                sb.append(pagamento.pagamentoEmprestimo()).append("\n");
            }
        } catch (Exception e) {
            sb.append("Erro ao listar pagamentos: ").append(e.getMessage());
        }
        return sb.toString();
    }

    
}
