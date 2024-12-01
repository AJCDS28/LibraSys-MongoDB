package Controller;

import Bean.SessoesBean;
import Model.SessoesModel;
import com.mongodb.client.MongoDatabase;
import java.util.LinkedHashSet;
import Main.EntradaSaida;

public class SessoesController {

    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuSessoes();
        switch (opt) {
            case 1 -> createSessao(database);
            case 2 -> {
                String sessoes  = listarSessoes(database);
                if (sessoes .isEmpty()) {
                    EntradaSaida.showMessage("Não há sessões cadastradas.\n");
                    menu(database);
                } else {
                    EntradaSaida.showMessage(sessoes );
                    menu(database);
                }
            }
            case 3 -> updateSessao(database);
            case 4 -> deleteSessao(database);
        }
    }

    private void createSessao(MongoDatabase database) {
        int codigo = EntradaSaida.getNumber("Digite o código da sessão: ");
        String nome = EntradaSaida.getText("Digite o nome da sessão: ");
        SessoesBean sessao = new SessoesBean(codigo, nome);
        boolean success = SessoesModel.createSessao(sessao, database);

        if (success) {
            EntradaSaida.showMessage("Sessão cadastrada com sucesso!");
        } else {
            EntradaSaida.showMessage("Falha ao cadastrar a sessão");
        }
        menu(database);
    }

    private void updateSessao(MongoDatabase database) {
        String sessoes = listarSessoes(database);

        if (sessoes.isEmpty()) {
            EntradaSaida.showMessage("Não há sessoes cadastrados.\n");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(sessoes);

        Integer idSessao = EntradaSaida.getNumber("Digite o ID da sessão: ");
        boolean success = false;
        String resposta;

        do {
            int opc = EntradaSaida.getNumber("Qual dos dados gostaria de alterar? \n1 - Código\n2 - Nome");

            switch (opc) {
                case 1 -> {
                    Integer novoCodigo = EntradaSaida.getNumber("Digite o novo código: ");
                    success = SessoesModel.alterarSessao(idSessao, "codigo", novoCodigo, database);
                }
                case 2 -> {
                    String novoNome = EntradaSaida.getText("Digite o novo nome: ");
                    success = SessoesModel.alterarSessao(idSessao, "nome", novoNome, database);
                }
            }

            if (success) {
                EntradaSaida.showMessage("Sessão alterada com sucesso!");
            } else {
                EntradaSaida.showMessage("Falha ao alterar a sessão");
            }

            resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
        } while (resposta.equalsIgnoreCase("s"));

        menu(database);
    }

    private void deleteSessao(MongoDatabase database) {
        String sessoes = listarSessoes(database);

        if (sessoes.isEmpty()) {
            EntradaSaida.showMessage("Não há sessoes cadastrados.\n");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(sessoes);

        Integer idSessao = EntradaSaida.getNumber("Digite o ID da sessão a ser deletada: ");
        int associacao = SessoesModel.verificaAssociacao(idSessao, database);
        if(associacao <= 0){
            boolean succes = SessoesModel.excluirSessao(idSessao, database);
        
            if(succes) {
                  EntradaSaida.showMessage("Sessão excluida com sucesso!");
                  menu(database);
                  return;
             } else {
                 EntradaSaida.showMessage("Falha ao excluir a sessão");
                 menu(database);
                 return;
             }
        }
        EntradaSaida.showMessage("A sessao tem livros associados, não foi possivel realizar a exclusão!");
        
        menu(database);
    }

    public String listarSessoes(MongoDatabase database) {
        LinkedHashSet<SessoesBean> sessoes = SessoesModel.listarSessoes(database);
        StringBuilder sb = new StringBuilder();

        if (sessoes.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Sessões:\n\n");

            for (SessoesBean sessao : sessoes) {
                sb.append(sessao.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}
