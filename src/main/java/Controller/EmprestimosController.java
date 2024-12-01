package Controller;

import Bean.EmprestimosBean;
import Bean.LivrosBean;
import Bean.PagamentosBean;
import Model.EmprestimosModel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import Main.EntradaSaida;
import Model.ClientesModel;
import Model.LivrosModel;
import com.mongodb.client.MongoDatabase;

public class EmprestimosController {
    
    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuEmprestimos();
        switch (opt) {
            case 1 -> createEmprestimo(database);
            case 2 -> {
                if(listarEmprestimos(database).equals("")){
                    EntradaSaida.showMessage("Não há emprestimos cadastrados");
                    menu(database);
                } else{
                    EntradaSaida.showMessage(listarEmprestimos(database));
                    menu(database);
                }
            }
            case 3 -> atualizarEmprestimo(database);
            case 4 -> excluirEmprestimo(database);
            case 5 -> realizarDevolucao(database);
        }
    }
    
   private int createEmprestimo(MongoDatabase database) {
        LocalDate data = LocalDate.now(); 
        Date dataEmprestimo = Date.valueOf(data);
        int quantLivros;
        List<LivrosBean> livros = new ArrayList<>();

        LivrosController livroController = new LivrosController();
        int quantLivrosCadas = livroController.getQuantLivrosCadas(database);
        if(quantLivrosCadas == 0 ){
            EntradaSaida.showMessage("Não há livros cadastrados para realizar o emprestimo");
             return 0;
        }
        
        ClientesController clienteController = new ClientesController();
        int quantClientes = clienteController.getQuantClientesCadas(database);
        if (quantClientes == -1) {
            EntradaSaida.showMessage("Não há clientes cadastrados para realizar o emprestimo");
            return 0;
        }
        do {
            String sql = "Total de livros cadastrados: " + quantLivrosCadas + "\n\nQuantos livros serão emprestados?";
            quantLivros = EntradaSaida.getNumber(sql);

            if (quantLivros > quantLivrosCadas) {
                EntradaSaida.showMessage("A quantidade de livros a ser emprestada não pode ser maior que a quantidade total de livros cadastrados. Tente novamente.");
            }

        } while (quantLivros > quantLivrosCadas);

        Integer[] idLivrosArray = new Integer[quantLivros];

        for (int i = 0; i < quantLivros; i++) {
            if(livroController.listarLivros(database).equals("")){
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(database);
            return 0;
        }
            EntradaSaida.showMessage(livroController.listarLivros(database));
            Integer idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
            
            if (!LivrosModel.livroExiste(database, idLivro)) {
            EntradaSaida.showMessage("Livro com o ID informado não encontrado.\n");
            menu(database);
            return 0;
            }
            idLivrosArray[i] = idLivro;
        }

        List<LivrosBean> livrosInformacoes = livroController.getLivrosInformacoes(database, idLivrosArray);

        double valorEmprestimo = 0.0;
        for (LivrosBean livro : livrosInformacoes) {
            if (livro.getQuantidadeDisponivel() < 1) {
                EntradaSaida.showMessage("O livro " + livro.getTitulo() + " não está disponível para empréstimo.");
                menu(database);
                return 0;
            }
            valorEmprestimo += livro.getValor();
        }

        valorEmprestimo = Math.round(valorEmprestimo * 100.0) / 100.0;
        
        if(ClientesController.listarClientes(database).equals("")){
            EntradaSaida.showMessage("Não clientes cadastrados");
            menu(database);
            return 0;
        }
        EntradaSaida.showMessage(ClientesController.listarClientes(database));
        Integer idCliente = EntradaSaida.getNumber("Digite o id do cliente: ");
        
        if (!ClientesModel.clienteExiste(database, idCliente)) {
            EntradaSaida.showMessage("Cliente com o ID informado não encontrado.\n");
            menu(database);
            return 0;
        }

        Date dataDevolucao = EntradaSaida.getDate("Digite a data de devolução (dd/MM/yyyy): ");
        java.util.Date hoje = new java.util.Date(System.currentTimeMillis());
        
        if (dataDevolucao.before(hoje)) {
            EntradaSaida.showMessage("A data de devolução deve ser maior que a data atual.");
            menu(database); 
            return 0;
        }
        
        valorEmprestimo = Math.round(valorEmprestimo * 100.0) / 100.0;
        int tipoPagamento = EntradaSaida.getNumber("Qual a forma de pagamento?\n1- À vista\n2- Parcelado\n3- Na devolução");

        StringBuilder resumoEmprestimo = new StringBuilder();
        resumoEmprestimo.append("Resumo do Empréstimo:\n");
        resumoEmprestimo.append("Cliente ID: ").append(idCliente).append("\n");
        resumoEmprestimo.append("Data de Devolução: ").append(new SimpleDateFormat("dd/MM/yyyy").format(dataDevolucao)).append("\n");
        resumoEmprestimo.append("Valor Total do Empréstimo: R$ ").append(valorEmprestimo).append("\n");
        resumoEmprestimo.append("Forma de Pagamento: ");
        switch (tipoPagamento) {
            case 1 -> resumoEmprestimo.append("À vista");
            case 2 -> resumoEmprestimo.append("Entrada");
            case 3 -> resumoEmprestimo.append("Na devolução");
        }
        resumoEmprestimo.append("\nLivros Emprestados: ");
        String livrosEmprestados = livrosInformacoes.stream()
            .map(LivrosBean::getTitulo)
            .collect(Collectors.joining(", "));

        resumoEmprestimo.append(livrosEmprestados).append("\n");

        EntradaSaida.showMessage(resumoEmprestimo.toString());

        String confirmar = EntradaSaida.getText("Deseja confirmar o empréstimo? (s/n)");
        if (confirmar.equalsIgnoreCase("s")) {
            EmprestimosBean emprestimo = new EmprestimosBean(idCliente, dataEmprestimo, dataDevolucao, 1, valorEmprestimo);
            Integer id_emprestimo = EmprestimosModel.createEmprestimo(emprestimo, idLivrosArray, database);

            if (id_emprestimo != 0) {
                PagamentosController pagamentosController = new PagamentosController();
                switch (tipoPagamento) {
                    case 1 -> {
                        PagamentosBean pagamentoAVista = new PagamentosBean( valorEmprestimo, 2, id_emprestimo);
                        pagamentosController.createPagamento(pagamentoAVista, database);
                        EntradaSaida.showMessage("Emprestimos realizado. Pagamento à vista registrado com sucesso!");
                    }

                    case 2 -> {
                        double valorEntrada = EntradaSaida.getDecimal("Qual o valor da entrada?");
                        
                        while(valorEntrada > valorEmprestimo){
                            EntradaSaida.showMessage("Valor invalido");
                            valorEntrada = EntradaSaida.getNumber("Qual o valor da entrada?");
                        }
                        
                        PagamentosBean pagamentoParcelado = new PagamentosBean( valorEntrada, 1, id_emprestimo);
                        pagamentosController.createPagamento(pagamentoParcelado, database);
                        
                        EntradaSaida.showMessage("Emprestimos realizado. Pagamento da entrada registrado com sucesso!");
                    }
                    default -> EntradaSaida.showMessage("Emprestimos realizado com sucesso.");
                }
            } else {
                EntradaSaida.showMessage("Falha ao cadastrar o empréstimo.");
            }
        } else {
            EntradaSaida.showMessage("Empréstimo cancelado.");
        }
        menu(database);
        return 1;
    }
   
   public String listarEmprestimos(MongoDatabase database) {
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimos(database);

        StringBuilder sb = new StringBuilder();

        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos:\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public String listarEmprestimosNãoQuitados(MongoDatabase database) {
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimosSemPagamento(database);

        StringBuilder sb = new StringBuilder();
        
        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos pendentes de pagamento:\n\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public String listarEmprestimosPendentes(MongoDatabase database){
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimosPendentes(database);

        StringBuilder sb = new StringBuilder();

        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos pendentes:\n\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public void atualizarEmprestimo(MongoDatabase database){
       if(listarEmprestimos(database).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(database);
            return;
        }
       EntradaSaida.showMessage(listarEmprestimos(database));
       
       Integer idEmprestimo = EntradaSaida.getNumber("Digite o id do emprestimo: ");
       
        if (!EmprestimosModel.emprestimoExiste(database, idEmprestimo)) {
            EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
            menu(database);
            return;
        }
       Date dataDevolucao = EntradaSaida.getDate("Digite a data de devolução (dd/MM/yyyy): ");
       java.sql.Date hoje = new java.sql.Date(System.currentTimeMillis());
        if (dataDevolucao.before(hoje)) {
            EntradaSaida.showMessage("A data de devolução deve ser maior que a data atual.");
            menu(database); 
            return;
        }
       boolean success = EmprestimosModel.alterarEmprestimo(idEmprestimo, dataDevolucao, database);
       
       if(success){
           EntradaSaida.showMessage("Emprestimo atualizado com sucesso");
           menu(database);
           return;
       }
       EntradaSaida.showMessage("Não foi possivel atualizar o emprestimo");
       menu(database);
   }
   
   public void excluirEmprestimo(MongoDatabase database) {
       if(listarEmprestimos(database).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(listarEmprestimos(database));

        Integer idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo que deseja excluir: ");
        if (!EmprestimosModel.emprestimoExiste(database, idEmprestimo)) {
           EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
           menu(database);
           return;
       }

        if (EmprestimosModel.temPagamentosRelacionados(idEmprestimo, database)) {
            EntradaSaida.showMessage("Não é possível excluir o empréstimo, pois há pagamentos associados.");
            menu(database);
            return;
        }

        boolean sucesso = EmprestimosModel.excluirEmprestimo(idEmprestimo, database);

        if (sucesso) {
            EntradaSaida.showMessage("Empréstimo excluído com sucesso.");
            menu(database);
            return;
        }
        EntradaSaida.showMessage("Não foi possível excluir o empréstimo.");
        menu(database);
    }
   
   public static void atualizarEmprestimos(MongoDatabase database) {
       EmprestimosModel.atualizarStatusEmprestimos(database);
   }
   
   public void realizarDevolucao(MongoDatabase database) {
        if(listarEmprestimosPendentes(database).equals("")){
            EntradaSaida.showMessage("Não há emprestimos pendentes");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(listarEmprestimosPendentes(database));
        Integer idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo a ser devolvido: ");
        
        if (!EmprestimosModel.emprestimoExiste(database, idEmprestimo)) {
           EntradaSaida.showMessage("Emprestimo com o ID informado não encontrado.\n");
           menu(database);
           return;
       }
        
        if (!EmprestimosModel.verificarEmprestimoPago(idEmprestimo, database)) {
            EntradaSaida.showMessage("O empréstimo não pode ser devolvido, pois ainda não foi totalmente pago.");
            menu(database);
            return;
        }
        boolean succes = EmprestimosModel.darBaixaEmprestimo(idEmprestimo, database);

        if(succes) {
            EntradaSaida.showMessage("Emprestimo devolvido com sucesso");
        } else {
            EntradaSaida.showMessage("Não foi possivel realizar a devolução");
        }
        menu(database);
    }
    
}
