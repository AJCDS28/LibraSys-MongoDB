package Model;

import Bean.RelatorioClientesInadimplentesBean;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import org.bson.Document;
import java.util.LinkedHashSet;

public class ListarClientesInadimplentesModel {

    public static LinkedHashSet<RelatorioClientesInadimplentesBean> listarClientesInadimplentes(MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        AggregateIterable<Document> resultados = emprestimosCollection.aggregate(Arrays.asList(
            new Document("$lookup", new Document()
                .append("from", "clientes")
                .append("localField", "id_cliente")
                .append("foreignField", "id_cliente")
                .append("as", "cliente")),

            new Document("$unwind", "$cliente"),

            new Document("$lookup", new Document()
                .append("from", "pagamentos")
                .append("localField", "id_emprestimo")
                .append("foreignField", "id_emprestimo")
                .append("as", "pagamentos")),

            new Document("$addFields", new Document()
                .append("valor_pago", new Document("$sum", "$pagamentos.valor_pago"))),

            new Document("$addFields", new Document()
                .append("valor_pendente", new Document("$subtract", Arrays.asList("$valor_emprestimo", "$valor_pago")))),

            new Document("$match", new Document("valor_pendente", new Document("$gt", 0))),

            new Document("$project", new Document()
                .append("nome_cliente", "$cliente.nome")
                .append("id_emprestimo", "$id_emprestimo")
                .append("valor_emprestimo", 1)
                .append("valor_pago", 1)
                .append("valor_pendente", 1)),

            new Document("$sort", new Document("nome_cliente", 1))
        ));

        LinkedHashSet<RelatorioClientesInadimplentesBean> listaInadimplentes = new LinkedHashSet<>();

        for (Document doc : resultados) {
            Number valorEmprestimo = doc.get("valor_emprestimo", Number.class);
            Number valorPago = doc.get("valor_pago", Number.class);
            Number valorPendente = doc.get("valor_pendente", Number.class);

            double valorEmprestimoDouble = (valorEmprestimo != null) ? valorEmprestimo.doubleValue() : 0.0;
            double valorPagoDouble = (valorPago != null) ? valorPago.doubleValue() : 0.0;
            double valorPendenteDouble = (valorPendente != null) ? valorPendente.doubleValue() : 0.0;

            RelatorioClientesInadimplentesBean inadimplente = new RelatorioClientesInadimplentesBean(
                doc.getString("nome_cliente"),
                doc.getInteger("id_emprestimo"),
                valorEmprestimoDouble,
                valorPagoDouble,
                valorPendenteDouble
            );
            listaInadimplentes.add(inadimplente);
        }

        return listaInadimplentes;
    }
}
