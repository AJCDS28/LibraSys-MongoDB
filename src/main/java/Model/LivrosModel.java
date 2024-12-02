package Model;

import Bean.LivrosBean;
import Main.IdGenerator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import org.bson.Document;

public class LivrosModel {
    
    public static boolean createLivro(LivrosBean livro, MongoDatabase database) {
        try {
            MongoCollection<Document> collection = database.getCollection("livros");
            IdGenerator idGen = new IdGenerator(database);
            Integer idLivro = idGen.getNextId("livroId");

            Document livroDoc = new Document("id_livro", idLivro)
                    .append("titulo", livro.getTitulo())
                    .append("data_publicacao", livro.getAnoPublicacao())
                    .append("quantidade_disponivel", livro.getQuantidadeDisponivel())
                    .append("quantidade_total", livro.getQuantidadeTotal())
                    .append("valor", livro.getValor())
                    .append("nome_autor", livro.getNomeAutor())
                    .append("id_sessao", livro.getIdSessao());

            collection.insertOne(livroDoc);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar livro: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean alterarLivro(Integer idLivro, double newValue, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("livros");
            
        Document filter = new Document("id_livro", idLivro);

        Document update = new Document("$set", new Document("valor", newValue));
        try {
            long count = collection.updateOne(filter, update).getModifiedCount();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }
    
    public static int verificaAssociacaoEmprestimos(Integer idAutor, MongoDatabase database) {
        try {
            MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
            MongoCollection<Document> concessaoCollection = database.getCollection("concessao");
            
            Document filterConcessao = new Document("id_livro",idAutor);
            Document concessao = concessaoCollection.find(filterConcessao).first();
            
            if (concessao != null) {
                Document filterEmprestimo = new Document("id_emprestimo", concessao.get("id_emprestimo"));
                long count = emprestimosCollection.countDocuments(filterEmprestimo);
                return (int) count;
            }
            
            return 0;
        } catch (Exception e) {
            System.err.println("Erro ao verificar associação: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean deleteLivro(Integer idLivro, MongoDatabase database) {
        try {
            MongoCollection<Document> livrosCollection = database.getCollection("livros");
            
            Document filter = new Document("id_livro", idLivro);
            
            long result = livrosCollection.deleteOne(filter).getDeletedCount();
            
            return result > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir o livro: " + e.getMessage());
            return false;
        }
    }

    public static List<LivrosBean> listarLivros(MongoDatabase database) {
        List<LivrosBean> livros = new ArrayList<>();
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");
        
        try {
            for (Document doc : livrosCollection.find()) {
                Integer idLivro = doc.getInteger("id_livro");
                String titulo = doc.getString("titulo");
                java.sql.Date data = new java.sql.Date(doc.getDate("data_publicacao").getTime());
                int disponivel = doc.getInteger("quantidade_disponivel", 0);
                int total = doc.getInteger("quantidade_total", 0);
                double valor = doc.getDouble("valor");
                String autor = doc.getString("nome_autor");

                Integer idSessao = doc.getInteger("id_sessao");
                String nomeSessao = null;
                Integer codigoSessao = null;
                
                if (idSessao != null) {
                    Document sessaoDoc = sessoesCollection.find(new Document("id_sessao", idSessao)).first();
                    if (sessaoDoc != null) {
                        nomeSessao = sessaoDoc.getString("nome");
                        codigoSessao = sessaoDoc.getInteger("codigo");
                    }
                }
                if (codigoSessao == null) {
                    codigoSessao = 0;
                    nomeSessao = "Sem Sessão";
                }

                livros.add(new LivrosBean(idLivro, titulo, data, disponivel, total, valor, autor,codigoSessao, nomeSessao));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        
        return livros;
    }
    
    public static LinkedHashSet<LivrosBean> listarLivrosSemSessao(MongoDatabase database) {
        LinkedHashSet<LivrosBean> livros = new LinkedHashSet<>();
        
        try {
            MongoCollection<Document> livrosCollection = database.getCollection("livros");
            
            Document query = new Document("id_sessao", 0);
            
            for (Document doc : livrosCollection.find(query)) {
                
                Integer idLivro = doc.getInteger("id_livro");
                String titulo = doc.getString("titulo");
                java.util.Date data = doc.getDate("data_publicacao");
                int disponivel = doc.getInteger("quantidade_disponivel");
                int total = doc.getInteger("quantidade_total");
                double valor = doc.getDouble("valor");
                String autor = doc.getString("nome_autor");

                LivrosBean livro = new LivrosBean(idLivro, titulo, data, disponivel, total, valor, autor, -1, null);

                livros.add(livro);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar livros sem sessão: " + e.getMessage());
        }

        return livros;
    }

    
    public static double somarValoresLivros(long[] idsLivros, MongoDatabase database) {
        double total = 0;
        
        try {
            MongoCollection<Document> livrosCollection = database.getCollection("livros");
            
            Document query = new Document("id_livro", new Document("$in", Arrays.asList(idsLivros)));
            
            for (Document doc : livrosCollection.find(query)) {
                total += doc.getDouble("valor");
            }
        } catch (Exception e) {
            System.err.println("Erro ao somar valores dos livros: " + e.getMessage());
        }

        return total;
    }
    
    public static int getQuantLivros(MongoDatabase database) {
        try {
            MongoCollection<Document> livrosCollection = database.getCollection("livros");
            
            long count = livrosCollection.countDocuments();

            return (int) count;
        } catch (Exception e) {
            System.err.println("Erro ao verificar quantidades de livros: " + e.getMessage());
            return 0;
        }
    }
    
    public static List<LivrosBean> getLivrosInformacoes(MongoDatabase database, Integer[] idsLivros) {
        List<LivrosBean> livros = new ArrayList<>();
        
        MongoCollection<Document> livrosCollection = database.getCollection("livros");

        try {
            List<Long> idsList = new ArrayList<>();
            for (long id : idsLivros) {
                idsList.add(id);
            }

            for (Document doc : livrosCollection.find(new Document("id_livro", new Document("$in", idsList)))) {
                Integer idLivro = doc.getInteger("id_livro");
                String titulo = doc.getString("titulo");
                int quantidadeDisponivel = doc.getInteger("quantidade_disponivel");
                double valor = doc.getDouble("valor");

                livros.add(new LivrosBean(idLivro, titulo, quantidadeDisponivel, valor));
            }

        } catch (Exception e) {
            System.err.println("Erro ao obter informações dos livros: " + e.getMessage());
        }

        return livros;
    }
    
    public static boolean associarSessao(long idLivro, Integer idSessao, MongoDatabase database) {
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");

        try {
            Document sessaoDoc = sessoesCollection.find(Filters.eq("id_sessao", idSessao)).first();
            if (sessaoDoc == null) {
                System.err.println("Sessão com ID " + idSessao + " não encontrada.");
                return false;
            }

            Document filtro = new Document("id_livro", idLivro);
            Document atualizacao = new Document("$set", new Document("id_sessao", idSessao));

            UpdateResult result = livrosCollection.updateOne(filtro, atualizacao);

            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao associar a sessão: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean livroExiste(MongoDatabase database, Integer idLivro) {
        return database.getCollection("livros")
                .find(new Document("id_livro", idLivro))
                .iterator()
                .hasNext();
    }
}