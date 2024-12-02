package Main;

import Bean.RelatorioClientesInadimplentesBean;
import Bean.RelatorioEmprestimosAtrasadosBean;
import Bean.RelatorioLivrosSessaoBean;
import Controller.ClientesController;
import Model.ClientesModel;
import Model.ListarClientesInadimplentesModel;
import Model.RelatorioEmprestimosAtrasadosModel;
import Model.RelatorioLivrosSessaoModel;
import com.mongodb.client.MongoDatabase;
import java.util.LinkedHashSet;

public class Relatorios {

    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuRelatorio();
        switch (opt) {
            case 1 -> {
                EntradaSaida.showMessage(relatorioLivrosSessao(database));
                menu(database);
            }
            case 2 -> {
                EntradaSaida.showMessage(listarEmprestimosAtrasados(database));
                menu(database);
            }
            case 3 -> {
                EntradaSaida.showMessage(listarClientesInadimplentes(database));
                menu(database);
            }
        }
    }
    
    public static String relatorioLivrosSessao(MongoDatabase database) {
        LinkedHashSet<RelatorioLivrosSessaoBean> sessoesComLivros = RelatorioLivrosSessaoModel.listarLivrosPorSessao(database);

        StringBuilder sb = new StringBuilder();
        if (sessoesComLivros.isEmpty()) {
            sb.append("Não há sessões ou livros cadastrados.\n");
        } else {
            sb.append("Relatório de Livros por Sessão:\n\n");
            for (RelatorioLivrosSessaoBean sessao : sessoesComLivros) {
                sb.append(sessao.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public String listarEmprestimosAtrasados(MongoDatabase database) {
        StringBuilder sb = new StringBuilder();
        if(ClientesController.listarClientes(database).equals("")){
            sb.append("Não há clientes cadastrados\n");
            return sb.toString();
        }
        EntradaSaida.showMessage(ClientesController.listarClientes(database));
        Integer idCliente = EntradaSaida.getNumber("Digite o id do cliente: ");
        if (!ClientesModel.clienteExiste(database, idCliente)) {
            sb.append("Cliente com o ID informado não encontrado.\n");
            return sb.toString();
        }
        LinkedHashSet<RelatorioEmprestimosAtrasadosBean> emprestimosAtrasados = RelatorioEmprestimosAtrasadosModel.listarEmprestimosAtrasadosPorCliente(idCliente, database);

        if (emprestimosAtrasados.isEmpty()) {
            sb.append("Não há empréstimos atrasados para o cliente informado.\n");
        } else {
            sb.append("Relatório de Empréstimos Atrasados:\n\n");
            for (RelatorioEmprestimosAtrasadosBean cliente : emprestimosAtrasados) {
                sb.append(cliente.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public static String listarClientesInadimplentes(MongoDatabase database) {
        LinkedHashSet<RelatorioClientesInadimplentesBean> clientesInadimplentes = ListarClientesInadimplentesModel.listarClientesInadimplentes(database);

        StringBuilder sb = new StringBuilder();

        if (clientesInadimplentes.isEmpty()) {
            sb.append("Não há clientes inadimplentes.\n");
        } else {
            sb.append("Relatório de Clientes Inadimplentes:\n\n");
            for (RelatorioClientesInadimplentesBean cliente : clientesInadimplentes) {
                sb.append(cliente.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}

