package Main;
import com.mongodb.client.MongoDatabase;
import Conexao.Conexao;
import Controller.ClientesController;
import Controller.EmprestimosController;
import Controller.LivrosController;
import Controller.PagamentosController;
import Controller.SessoesController;

public class Principal {

    public static void main(String[] args) {
        Conexao c = new Conexao();
        MongoDatabase database = c.getDatabase();

        try {
            EmprestimosController.atualizarEmprestimos(database);
            InicializacaoContadores.inicializarContadores(database);
            int op;
            do {
                op = EntradaSaida.getMenu();
                switch (op) {
                    case 1 -> new SessoesController().menu(database);
                    case 2 -> new LivrosController().menu(database);
                    case 3 -> new ClientesController().menu(database);
                    case 4 -> new EmprestimosController().menu(database);
                    case 5 -> new PagamentosController().menu(database);
                    case 6 -> new Relatorios().menu(database);
                }
            } while (op >= 1 && op <= 7);

        } catch (Exception ex) {
            System.out.println("Erro ao executar a operação: " + ex.getMessage());
        } finally {
            c.closeConnection();
        }
    }
}