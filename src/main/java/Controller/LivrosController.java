package Controller;

import Main.EntradaSaida;
import Bean.LivrosBean;
import Model.LivrosModel;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.bson.Document;
import java.util.List;

public class LivrosController {

    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuLivros();
        switch (opt) {
            case 1 -> createLivro(database);
            case 2 -> {
                if (listarLivros(database).equals("")) {
                    EntradaSaida.showMessage("Não há livros cadastrados.\n");
                    menu(database);
                } else {
                    EntradaSaida.showMessage(listarLivros(database));
                    menu(database);
                }
            }
            case 3 -> updateLivro(database);
            case 4 -> deleteLivro(database);
            case 5 -> associarSessao(database);
        }
    }

    private void createLivro(MongoDatabase database) {
        boolean success = false;
        String titulo = EntradaSaida.getText("Digite o nome do livro: ");
        Date data = EntradaSaida.getDate("Digite a data de publicação do livro: ");
        int quant_total = EntradaSaida.getNumber("Digite a quantidade total de livros");
        int quant_disponivel;
        Integer idSessao = 0;

        do {
            quant_disponivel = EntradaSaida.getNumber("Digite a quantidade de livros disponiveis");
            if (quant_disponivel > quant_total) {
                EntradaSaida.showMessage("Quantidade maior que a quantidade total!");
            }
        } while (quant_disponivel > quant_total);

        Double valor = EntradaSaida.getDecimal("Digite o valor do livro");
        String autor = EntradaSaida.getText("Digite o nome do autor do livro: ");
        String issessao = EntradaSaida.getText("Deseja adicioná-lo a uma sessão? (s/n)");

        if (issessao.equalsIgnoreCase("s")) {
            SessoesController sessao = new SessoesController();
            String sessoes = sessao.listarSessoes(database);
            if (sessoes.isEmpty()) {
                EntradaSaida.showMessage("Não há sessões cadastradas.\n");
            } else {
                EntradaSaida.showMessage(sessoes);
                idSessao = EntradaSaida.getNumber("Digite o ID da sessão:");
            }
        }

        LivrosBean livro = new LivrosBean(titulo, data, quant_disponivel, quant_total, valor, autor, idSessao);

        success = LivrosModel.createLivro(livro, database);

        if (success) {
            EntradaSaida.showMessage("Livro cadastrado com sucesso!");
        } else {
            EntradaSaida.showMessage("Falha ao cadastrar o livro");
        }
        menu(database);
    }

    public String listarLivros(MongoDatabase database) {
        List<LivrosBean> livros = LivrosModel.listarLivros(database);

        StringBuilder sb = new StringBuilder();
        
        if (livros.isEmpty()) {
            sb.append("");
        } else{
            sb.append("Lista de Livros:\n");

            for (LivrosBean autor : livros) {
                sb.append(autor.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public String listarLivrosSemSessao(MongoDatabase database){
        LinkedHashSet<LivrosBean> all = LivrosModel.listarLivrosSemSessao(database);

        StringBuilder sb = new StringBuilder();
        
        if (all.isEmpty()) {
            return "";
        } else{
            sb.append("Lista de Livros sem sessão:\n");

            for (LivrosBean autor : all) {
                sb.append(autor.toString()).append("\n");
            }
        }
        return sb.toString();
    }
    
    private void updateLivro(MongoDatabase database) {
        if (listarLivros(database).equals("")) {
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(database);
            return;
        }
        boolean success = false;
        String resposta;
        EntradaSaida.showMessage(listarLivros(database));
        Integer idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
        
        if (!LivrosModel.livroExiste(database, idLivro)) {
            EntradaSaida.showMessage("Livro com o ID informado não encontrado.\n");
            menu(database);
            return;
        }
        do {
            double novoValor = EntradaSaida.getDecimal("Digite o novo valor: ");
            success = LivrosModel.alterarLivro(idLivro, novoValor, database);

            if (success) {
                EntradaSaida.showMessage("Livro alterado com sucesso!");
                resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
            } else {
                EntradaSaida.showMessage("Falha ao alterar o livro");
                resposta = "n";
                menu(database);
                return;
            }
        } while (resposta.equalsIgnoreCase("s"));
        menu(database);
    }

    private void deleteLivro(MongoDatabase database) {
        if (listarLivros(database).equals("")) {
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(database);
            return;
        }
        
        EntradaSaida.showMessage(listarLivros(database));
        Integer idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
        
        if (!LivrosModel.livroExiste(database, idLivro)) {
            EntradaSaida.showMessage("Livro com o ID informado não encontrado.\n");
            menu(database);
            return;
        }
        
        int associacao = LivrosModel.verificaAssociacaoEmprestimos(idLivro, database);
        if(associacao <=0) {
            boolean succes = LivrosModel.deleteLivro(idLivro, database);

            if(succes) {
                  EntradaSaida.showMessage("Livro excluido com sucesso!");
                  menu(database);
                  return;
             } else {
                 EntradaSaida.showMessage("Falha ao excluir o livro");
                 menu(database);
                 return;
             }
        }
        EntradaSaida.showMessage("Livro esta associado a um empréstimo, não é possivel removê-lo");
        menu(database);
    }

    private void associarSessao(MongoDatabase database) {

        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        List<Document> livrosSemSessao = livrosCollection.find(new Document("id_sessao", 0)).into(new ArrayList<>());

        if (livrosSemSessao.isEmpty()) {
            EntradaSaida.showMessage("Não há livros sem sessão.");
            menu(database);
            return;
        }

        EntradaSaida.showMessage(listarLivrosSemSessao(database));

        Integer idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");

        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");
        List<Document> sessoes = sessoesCollection.find().into(new ArrayList<>());

        if (sessoes.isEmpty()) {
            EntradaSaida.showMessage("Não há sessões cadastradas.\n");
            menu(database);
            return;
        }
        SessoesController sessao = new SessoesController();
        EntradaSaida.showMessage(sessao.listarSessoes(database));

        Integer idSessao = EntradaSaida.getNumber("Digite o ID da sessão:");

        boolean success = LivrosModel.associarSessao(idLivro, idSessao, database);

        if(success) {
            EntradaSaida.showMessage("Sessão associada ao livro com sucesso!");
            menu(database);
            return;
        }
        EntradaSaida.showMessage("Falha ao associar sessão ao livro");
        menu(database);
    }


    public double mostrarSomaValoresLivros(MongoDatabase database, long[] idsLivros) {
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        double total = 0;
        for (long id : idsLivros) {
            Document query = new Document("_id", id);
            Document livro = livrosCollection.find(query).first();
            if (livro != null) {
                total += livro.getDouble("valor");
            }
        }
        return total;
    }
    
    public int getQuantLivrosCadas(MongoDatabase database) {
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        long count = livrosCollection.countDocuments();
        return (int) count;
    }

    public List<LivrosBean> getLivrosInformacoes(MongoDatabase database, Integer[] idsLivros) {
        try {
            return LivrosModel.getLivrosInformacoes(database, idsLivros);
        } catch (Exception e) {
            EntradaSaida.showMessage("Erro ao obter informações dos livros.");
        }
        return Collections.emptyList();
    }
}
