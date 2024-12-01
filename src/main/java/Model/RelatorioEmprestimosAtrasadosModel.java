package Model;

import Bean.RelatorioEmprestimosAtrasadosBean;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.LinkedHashSet;
import java.util.Date;

public class RelatorioEmprestimosAtrasadosModel {

        public static LinkedHashSet<RelatorioEmprestimosAtrasadosBean> listarEmprestimosAtrasadosPorCliente(Integer idCliente, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> clientesCollection = database.getCollection("clientes");
        MongoCollection<Document> concessaoCollection = database.getCollection("concessao");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");

        FindIterable<Document> emprestimos = emprestimosCollection.find(new Document("id_cliente", idCliente)
                .append("status", 3));

        LinkedHashSet<RelatorioEmprestimosAtrasadosBean> emprestimosAtrasados = new LinkedHashSet<>();

        for (Document emprestimo : emprestimos) {
            Integer idEmprestimo = emprestimo.getInteger("id_emprestimo");
            double valorEmprestimo = emprestimo.getDouble("valor_emprestimo");
            Date dataDevolucao = emprestimo.getDate("data_devolucao");

            Document cliente = clientesCollection.find(new Document("id_cliente", idCliente)).first();
            String nomeCliente = (cliente != null) ? cliente.getString("nome") : "Desconhecido";

            String tituloLivro = getTituloLivroById(idEmprestimo, concessaoCollection, livrosCollection);

            RelatorioEmprestimosAtrasadosBean clienteBean = emprestimosAtrasados.stream()
                    .filter(bean -> bean.getNomeCliente().equals(nomeCliente))
                    .findFirst()
                    .orElse(null);

            if (clienteBean == null) {
                clienteBean = new RelatorioEmprestimosAtrasadosBean(nomeCliente);
                emprestimosAtrasados.add(clienteBean);
            }

            clienteBean.addEmprestimo(idEmprestimo, valorEmprestimo, tituloLivro, dataDevolucao);
        }

        return emprestimosAtrasados;
    }

    private static String getTituloLivroById(Integer idEmprestimo, MongoCollection<Document> concessaoCollection, MongoCollection<Document> livrosCollection) {
        Document concessao = concessaoCollection.find(new Document("id_emprestimo", idEmprestimo)).first();
        if (concessao != null) {
            Integer idLivro = concessao.getInteger("id_livro");

            Document livro = livrosCollection.find(new Document("id_livro", idLivro)).first();
            if (livro != null) {
                return livro.getString("titulo");
            }
        }
        return "Indisponível";
    }
}
