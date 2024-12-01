package Conexao;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Conexao {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "LibraSys";

    private MongoClient mongoClient;
    private MongoDatabase database;

    public Conexao() {
        try {
            MongoClientURI clientURI = new MongoClientURI(URI);
            mongoClient = new MongoClient(clientURI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("Conectado com sucesso ao MongoDB!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexão com o MongoDB encerrada.");
        }
    }
}
