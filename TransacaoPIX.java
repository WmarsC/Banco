import java.time.LocalDateTime;

public class TransacaoPIX {
    private String destinatario;
    private String remetente;
    private double valor;
    private String mensagem;
    private LocalDateTime dataHora;

    public TransacaoPIX(String remetente, String destinatario, double valor, String mensagem) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.valor = valor;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getRemetente() {
        return remetente;
    }

    public double getValor() {
        return valor;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
