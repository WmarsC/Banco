public abstract class Conta {
    protected String numero;
    protected String titular;
    protected double saldo;
    protected List<TransacaoPIX> historicoPix = new ArrayList<>();

    public Conta(String numero, String titular) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = 0.0;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public boolean sacar(double valor) {
        if (saldo >= valor) {
            saldo -= valor;
            return true;
        }
        return false;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getTipoConta() {
        return this instanceof ContaCorrente ? "Corrente" : "Poupança";
    }

    public void adicionarTransacaoPix(TransacaoPIX t) {
        historicoPix.add(t);
    }

    public List<TransacaoPIX> getHistoricoPix() {
        return historicoPix;
    }
}
