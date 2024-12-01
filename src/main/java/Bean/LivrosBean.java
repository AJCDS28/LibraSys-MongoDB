package Bean;
import java.util.Date;

public class LivrosBean {

    private Integer idLivro;
    private String titulo;
    private Date anoPublicacao;
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private double valor;
    private String nomeAutor;
    private Integer idSessao; 
    private String nomeSessao;
    private Integer codigoSessao;

    public LivrosBean(Integer idLivro, String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor, Integer idSessao) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.idSessao = idSessao;
    }
    
    public LivrosBean( String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor, Integer idSessao) {
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.idSessao = idSessao;
    }

    public LivrosBean(String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor) {
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
    }
    
    public LivrosBean(Integer idLivro, String titulo, Date dataPublicacao, int quantidadeDisponivel, int quantidadeTotal, double valor, String nomeAutor, Integer codigoSessao,  String nomeSessao) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.anoPublicacao = dataPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.codigoSessao = codigoSessao;
        this.nomeSessao = nomeSessao ;
    }
    
    public LivrosBean(Integer idLivro, Integer idSessao) {
        this.idLivro = idLivro;
        this.idSessao = idSessao;
    }
    
    public LivrosBean(Integer idLivro, String titulo, int quantidadeDisponivel, double valor) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.valor = valor;
    }
    
    public Integer getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Date anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public Integer getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(Integer idSessao) {
        this.idSessao = idSessao;
    }
    
    public String getCodigoENomeSessao() {
        if (codigoSessao <= 0) {
            return "Não associada";
        }
        return codigoSessao + " - " + nomeSessao;
    }

    @Override
    public String toString() {
        return "Id Livro: " + idLivro  
                + " | Título: " + titulo 
                + " | Ano de Publicação: " + anoPublicacao
                + " | Quantidade Total: " + quantidadeTotal 
                + " | Quantidade Disponível: " + quantidadeDisponivel 
                + " | Valor: R$" + valor 
                + " | Autor: " + nomeAutor
                + " | Sessão: " + getCodigoENomeSessao();
    }
}
