package Model;

import Bean.RelatorioLivrosSessaoBean;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class RelatorioLivrosSessaoModel {

    public static LinkedHashSet<RelatorioLivrosSessaoBean> listarLivrosPorSessao(MongoDatabase database) {
        MongoCollection<Document> sessoesCollection = database.getCollection("sessoes");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");

        FindIterable<Document> sessoes = sessoesCollection.find();

        LinkedHashSet<RelatorioLivrosSessaoBean> sessoesComLivros = new LinkedHashSet<>();
        List<String> titulosLivros = new ArrayList<>();
        Integer idSessaoAtual = -1;
        Integer codigoSessaoAtual = -1;
        String nomeSessaoAtual = null;

        for (Document sessao : sessoes) {
            Integer idSessao = sessao.getInteger("id_sessao");
            Integer codigoSessao = sessao.getInteger("codigo");
            String nomeSessao = sessao.getString("nome");

            FindIterable<Document> livros = livrosCollection.find(new Document("id_sessao", idSessao));

            if (!Objects.equals(idSessao, idSessaoAtual)) {
                if (idSessaoAtual != -1) {
                    sessoesComLivros.add(new RelatorioLivrosSessaoBean(idSessaoAtual, codigoSessaoAtual, nomeSessaoAtual, new ArrayList<>(titulosLivros)));
                }
                titulosLivros.clear();
                idSessaoAtual = idSessao;
                nomeSessaoAtual = nomeSessao;
                codigoSessaoAtual = codigoSessao;
            }

            for (Document livro : livros) {
                String tituloLivro = livro.getString("titulo");
                titulosLivros.add(tituloLivro);
            }
        }

        if (idSessaoAtual != -1) {
            sessoesComLivros.add(new RelatorioLivrosSessaoBean(idSessaoAtual, codigoSessaoAtual, nomeSessaoAtual, new ArrayList<>(titulosLivros)));
        }

        return sessoesComLivros;
    }
}
