package Model;

import Bean.SessoesBean;
import Main.IdGenerator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.LinkedHashSet;

public class SessoesModel {

    public static boolean createSessao(SessoesBean sessao, MongoDatabase database) {
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");
        IdGenerator idGen = new IdGenerator(database);
        Integer idCliente = idGen.getNextId("sessaoId");
        
        Document doc = new Document("id_sessao", idCliente)
                        .append("codigo", sessao.getCodigo())
                        .append("nome", sessao.getNome());
        
        try {
            sessoesCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar a sessao: " + e.getMessage());
            return false;
        }
    }

    public static boolean alterarSessao(Integer id_sessao, String coluna, Object new_value, MongoDatabase database) {
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");

        Document filter = new Document("id_sessao", id_sessao);
        Document update = new Document("$set", new Document(coluna, new_value));

        try {
            long count = sessoesCollection.updateOne(filter, update).getModifiedCount();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar sessao: " + e.getMessage());
            return false;
        }
    }

    public static int verificaAssociacao(Integer id_sessao, MongoDatabase database) {
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        Document filter = new Document("id_sessao", id_sessao);
        
        long count = livrosCollection.countDocuments(filter);
        
        return (int) count;
    }

    public static boolean excluirSessao(Integer id_sessao, MongoDatabase database) {
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");

        Document filter = new Document("id_sessao", id_sessao);

        try {
            long count = sessoesCollection.deleteOne(filter).getDeletedCount();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir a sessao: " + e.getMessage());
            return false;
        }
    }

    public static LinkedHashSet<SessoesBean> listarSessoes(MongoDatabase database) {
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");

        LinkedHashSet<SessoesBean> sessoes = new LinkedHashSet<>();

        for (Document doc : sessoesCollection.find()) {
            Integer idSessao = doc.getInteger("id_sessao");
            int codigo = doc.getInteger("codigo");
            String nome = doc.getString("nome");

            SessoesBean sessao = new SessoesBean(idSessao, codigo, nome);
            sessoes.add(sessao);
        }

        return sessoes;
    }
}
