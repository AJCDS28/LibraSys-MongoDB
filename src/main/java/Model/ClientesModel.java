package Model;

import Bean.ClientesBean;
import Main.IdGenerator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.LinkedHashSet;

public class ClientesModel {

    public static boolean createCliente(ClientesBean cliente, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("clientes");
        IdGenerator idGen = new IdGenerator(database);
        Integer idCliente = idGen.getNextId("clienteId");

        Document doc = new Document("id_cliente", idCliente)
            .append("nome", cliente.getNome())
            .append("email", cliente.getEmail())
            .append("telefone", cliente.getTelefone())
            .append("cpf", cliente.getCpf());

        try {
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            return false;
        }
    }

    public static boolean alterarCliente(Integer idCliente, String coluna, Object newValue, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("clientes");

        try {
            Document update = new Document("$set", new Document(coluna, newValue));
            UpdateResult result = collection.updateOne(Filters.eq("id_cliente", idCliente), update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public static boolean verificarEmprestimosAtivos(Integer idCliente, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        try {
            long count = emprestimosCollection.countDocuments(Filters.eq("id_cliente", idCliente));
            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao verificar empréstimos ativos: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteCliente(Integer idCliente, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("clientes");

        try {
            DeleteResult result = collection.deleteOne(Filters.eq("id_cliente", idCliente));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
            return false;
        }
    }

    public static LinkedHashSet<ClientesBean> listarClientes(MongoDatabase database) {
        MongoCollection<Document> clientesCollection = database.getCollection("clientes");

        LinkedHashSet<ClientesBean> clientes = new LinkedHashSet<>();

        for (Document doc : clientesCollection.find()) {
            Integer idCliente = doc.getInteger("id_cliente");
            String nome = doc.getString("nome");
            String email = doc.getString("email");
            String telefone = doc.getString("telefone");
            String cpf = doc.getString("cpf");

            ClientesBean cliente = new ClientesBean(idCliente, nome, email, telefone, cpf);
            clientes.add(cliente);
        }

        return clientes;
    }
    
    public static int getQuantClientes(MongoDatabase database) {
        try {
            MongoCollection<Document> clientesCollection = database.getCollection("clientes");
            long count = clientesCollection.countDocuments();
            return (int) count;
        } catch (Exception e) {
            System.err.println("Erro ao verificar quantidade de clientes: " + e.getMessage());
            return -1;
        }
    }
    
    public static boolean clienteExiste(MongoDatabase database, Integer idCliente) {
        return database.getCollection("clientes")
                .find(new Document("id_cliente", idCliente))
                .iterator()
                .hasNext();
    }
}
