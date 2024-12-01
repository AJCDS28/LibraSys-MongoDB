package Bean;

public class ClientesBean {
    private Integer id_cliente;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;

    public ClientesBean(Integer id_cliente, String nome, String email, String telefone, String cpf) {
        this.id_cliente = id_cliente;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public ClientesBean(String nome, String email, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public Integer getId() {
        return id_cliente;
    }

    public void setId(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "ID: " + id_cliente + " | " 
                + "Nome: " + nome + " | " 
                + "E-mail: " + email  + " | " 
                + "Telefone: " + telefone + " | " 
                + "CPF: " + cpf;
    }
}
