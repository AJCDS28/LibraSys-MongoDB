package Controller;

import Main.EntradaSaida;
import Bean.ClientesBean;
import Model.ClientesModel;
import com.mongodb.client.MongoDatabase;
import java.util.LinkedHashSet;

public class ClientesController {

    public void menu(MongoDatabase database) {
        int opt = EntradaSaida.getMenuClientes();
        switch (opt) {
            case 1 -> createCliente(database);
            case 2 -> {
                String clientesListados = listarClientes(database);
                if (clientesListados.isEmpty()) {
                    EntradaSaida.showMessage("Não há clientes cadastrados.\n");
                    menu(database);
                } else {
                    EntradaSaida.showMessage(clientesListados);
                    menu(database);
                }
            }
            case 3 -> updateCliente(database);
            case 4 -> deleteCliente(database);
        }
    }

    private void createCliente(MongoDatabase database) {
        String nome = EntradaSaida.getText("Digite o nome do cliente: ");
        String email = EntradaSaida.getText("Digite o email do cliente: ");
        String telefone = EntradaSaida.getText("Digite o telefone do cliente: ");
        String cpf = EntradaSaida.getText("Digite o cpf do cliente: ");
        ClientesBean cliente = new ClientesBean(nome, email, telefone, cpf);
        boolean success = ClientesModel.createCliente(cliente, database);

        if (success) {
            EntradaSaida.showMessage("Cliente cadastrado com sucesso!");
        } else {
            EntradaSaida.showMessage("Falha ao cadastrar o cliente");
        }
        menu(database);
    }

    private void updateCliente(MongoDatabase database) {
        String coluna, resposta;
        boolean success = false;
        String clientesListados = listarClientes(database);

        if (clientesListados.isEmpty()) {
            EntradaSaida.showMessage("Não há clientes cadastrados.\n");
            menu(database);
            return;
        }
        EntradaSaida.showMessage(clientesListados);

        Integer idCliente = EntradaSaida.getNumber("Digite o id do cliente a ser alterado: ");
        
        if (!ClientesModel.clienteExiste(database, idCliente)) {
            EntradaSaida.showMessage("Cliente com o ID informado não encontrado.\n");
            menu(database);
            return;
        }

        do {
            int opc = EntradaSaida.getNumber("Qual dado você gostaria de alterar? \n1 - Nome\n2 - Email\n3 - Telefone");

            switch (opc) {
                case 1 -> {
                    coluna = "nome";
                    String novoNome = EntradaSaida.getText("Digite o novo nome: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoNome, database);
                }
                case 2 -> {
                    coluna = "email";
                    String novoEmail = EntradaSaida.getText("Digite o novo email: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoEmail, database);
                }
                case 3 -> {
                    coluna = "telefone";
                    String novoTelefone = EntradaSaida.getText("Digite o novo telefone: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoTelefone, database);
                }
            }
            if (success) {
                EntradaSaida.showMessage("Cliente alterado com sucesso!");
            } else {
                EntradaSaida.showMessage("Falha ao alterar o cliente");
            }

            resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
        } while (resposta.equalsIgnoreCase("s"));

        menu(database);
    }

    private void deleteCliente(MongoDatabase database) {
        if(listarClientes(database).equals("")){
            EntradaSaida.showMessage("Não há clientes cadastrados.\n");
            menu(database);
            return;
        }       
        EntradaSaida.showMessage(listarClientes(database));
        Integer idCliente = EntradaSaida.getNumber("Digite o id do cliente a ser removido: ");
        
        if (!ClientesModel.clienteExiste(database, idCliente)) {
            EntradaSaida.showMessage("Cliente com o ID informado não encontrado.\n");
            menu(database);
            return;
        }
        
        boolean success = ClientesModel.deleteCliente(idCliente, database);

        if (success) {
            EntradaSaida.showMessage("Cliente removido com sucesso!");
        } else {
            EntradaSaida.showMessage("Falha ao remover o cliente");
        }
        menu(database);
    }

    public static String listarClientes(MongoDatabase database) {
        LinkedHashSet<ClientesBean> clientes = ClientesModel.listarClientes(database);
        StringBuilder sb = new StringBuilder();
        
        if (clientes.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Clientes:\n\n");

            for (ClientesBean cliente : clientes) {
                sb.append(cliente.toString()).append("\n");
            }
        }

        return sb.toString();
    }
    
    public int getQuantClientesCadas(MongoDatabase database) {
    try {
        return ClientesModel.getQuantClientes(database);
    } catch (Exception e) {
        EntradaSaida.showMessage("Erro ao verificar quantidades de clientes.");
    }
    return -1;
}
}