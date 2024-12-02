package Main;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class InicializacaoContadores {

    public static void inicializarContadores(MongoDatabase database) {
        MongoCollection<Document> countersCollection = database.getCollection("counters");

        if (countersCollection.find(new Document("_id", "clienteId")).first() == null) {
            countersCollection.insertOne(new Document("_id", "clienteId").append("seq", 0));
        }

        if (countersCollection.find(new Document("_id", "livroId")).first() == null) {
            countersCollection.insertOne(new Document("_id", "livroId").append("seq", 0));
        }
        
        if (countersCollection.find(new Document("_id", "sessaoId")).first() == null) {
            countersCollection.insertOne(new Document("_id", "sessaoId").append("seq", 0));
        }
        
        if (countersCollection.find(new Document("_id", "pagamentoId")).first() == null) {
            countersCollection.insertOne(new Document("_id", "pagamentoId").append("seq", 0));
        }
        
        if (countersCollection.find(new Document("_id", "emprestimoId")).first() == null) {
            countersCollection.insertOne(new Document("_id", "emprestimoId").append("seq", 0));
        }
    }
}
