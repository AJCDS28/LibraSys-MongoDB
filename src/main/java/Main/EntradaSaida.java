package Main;

import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

public class EntradaSaida {
    
     public static Integer getMenu() {
         
        StringBuilder menu = new StringBuilder();
        menu.append("\nLibraSys\n\n");
        menu.append("Informe o número do menu que deseja acessar:\n");
        menu.append("1 - Sessoes\n");
        menu.append("2 - Livros\n");
        menu.append("3 - Cliente\n");
        menu.append("4 - Emprestimos\n");
        menu.append("5 - Pagamentos\n");
        menu.append("6 - Relatórios\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuSessoes() {
        
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Sessões\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Cadastrar sessão\n");
        menu.append("2 - Listar sessões\n");
        menu.append("3 - Editar sessão\n");
        menu.append("4 - Excluir sessão\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuLivros() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Livros\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Cadastrar livro\n");
        menu.append("2 - Listar livros\n");
        menu.append("3 - Editar livro\n");
        menu.append("4 - Excluir livro\n");
        menu.append("5 - Associar livro a uma sessão\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuClientes() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Clientes\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Cadastrar cliente\n");
        menu.append("2 - Listar clientes\n");
        menu.append("3 - Editar cliente\n");
        menu.append("4 - Excluir cliente\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuEmprestimos() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Empréstimos\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Criar empréstimo\n");
        menu.append("2 - Listar empréstimos\n");
        menu.append("3 - Editar empréstimo\n");
        menu.append("4 - Excluir empréstimo\n");
        menu.append("5 - Baixar empréstimo\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuPagamentos() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Pagamentos\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Realizar pagamento\n");
        menu.append("2 - Listar pagamentos\n");
        menu.append("3 - Excluir pagamento\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static Integer getMenuRelatorio() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nMenu de Relatórios\n\n");
        menu.append("Selecione uma das opções:\n");
        menu.append("1 - Relatório de livros por sessões\n");
        menu.append("2 - Relatório de empréstimos atrasados por clientes\n");
        menu.append("3 - Relatório de emprestimos pendentes de pagamentos\n\n");
        menu.append("Digite qualquer outro valor para sair");

        return getNumber(menu.toString());
    }
    
    public static String getText(String msg) {
        return JOptionPane.showInputDialog(msg);
    }
    
    public static Integer getNumber(String msg) {
	while(true) {
            try {
		Integer number = Integer.valueOf(JOptionPane.showInputDialog(msg));
		if (number <= 0) throw new Exception();
                    return number;
            } catch (Exception e) {
		showMessage("Input inválido");
		}
            }
	}
    
    public static Date getDate(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        while (true) {
            try {
                String input = JOptionPane.showInputDialog(msg);
                java.util.Date parsedDate = dateFormat.parse(input);
                return new Date(parsedDate.getTime());
            } catch (Exception e) {
                showMessage("Input inválido. Formato esperado: dd/MM/yyyy");
            }
        }
    }
    
    public static Double getDecimal(String msg) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(msg);

                if (input == null) {
                    showMessage("Operação cancelada.");
                    return null;
                }

                Double decimalValue = Double.valueOf(input);

                if (decimalValue < 0) throw new Exception("O valor deve ser positivo.");

                return Math.round(decimalValue * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                showMessage("Input inválido. Insira um número válido.");
            } catch (Exception e) {
                showMessage(e.getMessage());
            }
        }
    }

    public static void showMessage(String msg) {
	JOptionPane.showMessageDialog(null, msg);
    }
    
}