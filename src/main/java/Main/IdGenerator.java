package Main;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import static com.mongodb.client.model.Updates.inc;

public class IdGenerator {

    private MongoDatabase database;

    public IdGenerator(MongoDatabase database) {
        this.database = database;
    }

    public int getNextId(String counterName) {
        MongoCollection<Document> counters = database.getCollection("counters");
        
        Document result = counters.findOneAndUpdate(
            eq("_id", counterName),
            inc("seq", 1),
            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        if (result != null) {
            return result.getInteger("seq");
        } else {
            throw new RuntimeException("Erro ao gerar o próximo identificador");
        }
    }
}
