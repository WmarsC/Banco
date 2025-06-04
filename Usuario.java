public class Usuario {
    private String nome;
    private String senha;
    private Conta conta;

    public Usuario(String nome, String senha, Conta conta) {
        this.nome = nome;
        this.senha = senha;
        this.conta = conta;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public Conta getConta() {
        return conta;
    }
}
