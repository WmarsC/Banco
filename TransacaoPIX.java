import java.time.LocalDateTime;

public class TransacaoPIX {
    private String destinatario;
    private double valor;
    private LocalDateTime dataHora;

    public TransacaoPIX(String destinatario, double valor) {
        this.destinatario = destinatario;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public String getDestinatario() {
        return destinatario;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
