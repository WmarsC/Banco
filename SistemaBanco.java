import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SistemaBanco {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n\u2554 BEM-VINDO AO BANCO \u2557");
            System.out.println("1. Criar conta");
            System.out.println("2. Login");
            System.out.println("3. Suporte");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> criarConta();
                case 2 -> login();
                case 3 -> Suporte.menuSuporte();
                case 4 -> {
                    System.out.println("Saindo do sistema...");
                    return;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void criarConta() {
        scanner.nextLine();
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.println("Tipo de conta: 1. Corrente | 2. Poupança");
        int tipo = scanner.nextInt();

        Conta conta;
        String numero = UUID.randomUUID().toString().substring(0, 8);
        conta = (tipo == 1) ? new ContaCorrente(numero, nome) : new ContaPoupanca(numero, nome);

        usuarios.add(new Usuario(nome, senha, conta));
        System.out.println("Conta criada com sucesso!");
    }

    private static void login() {
        scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = usuarios.stream()
                .filter(u -> u.getNome().equals(nome) && u.getSenha().equals(senha))
                .findFirst().orElse(null);

        if (usuario == null) {
            System.out.println("Credenciais inválidas!");
            return;
        }

        menuConta(usuario);
    }

    private static void menuConta(Usuario usuario) {
        int opcao;
        do {
            System.out.printf("\nUsuário: %s | Tipo: %s | Saldo: R$ %.2f\n",
                    usuario.getNome(), usuario.getConta().getTipoConta(), usuario.getConta().getSaldo());
            System.out.println("1. Depositar\n2. Sacar\n3. Fazer PIX\n4. Extrato PIX\n5. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> depositar(usuario);
                case 2 -> sacar(usuario);
                case 3 -> fazerPix(usuario);
                case 4 -> mostrarExtratoPix(usuario);
                case 5 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 5);
    }

    private static void depositar(Usuario usuario) {
        System.out.print("Valor: ");
        double valor = scanner.nextDouble();
        usuario.getConta().depositar(valor);
        System.out.println("Depósito realizado!");
    }

    private static void sacar(Usuario usuario) {
        System.out.print("Valor: ");
        double valor = scanner.nextDouble();
        if (usuario.getConta().sacar(valor)) {
            System.out.println("Saque realizado!");
        } else {
            System.out.println("Saldo insuficiente.");
        }
    }

    private static void fazerPix(Usuario remetente) {
        scanner.nextLine();
        System.out.print("Nome do destinatário: ");
        String nomeDestinatario = scanner.nextLine();

        Usuario destinatario = usuarios.stream()
                .filter(u -> u.getNome().equals(nomeDestinatario))
                .findFirst().orElse(null);

        if (destinatario == null) {
            System.out.println("Destinatário não encontrado.");
            return;
        }

        System.out.print("Valor do PIX: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Mensagem: ");
        String mensagem = scanner.nextLine();

        if (!remetente.getConta().sacar(valor)) {
            System.out.println("Saldo insuficiente.");
            return;
        }

        destinatario.getConta().depositar(valor);

        TransacaoPIX t = new TransacaoPIX(remetente.getNome(), destinatario.getNome(), valor, mensagem);
        remetente.getConta().adicionarTransacaoPix(t);
        destinatario.getConta().adicionarTransacaoPix(t);

        System.out.println("PIX enviado!");
    }

    private static void mostrarExtratoPix(Usuario usuario) {
        List<TransacaoPIX> historico = usuario.getConta().getHistoricoPix();
        LocalDateTime agora = LocalDateTime.now();

        List<TransacaoPIX> recentes = historico.stream()
                .filter(t -> t.getRemetente().equals(usuario.getNome()))
                .filter(t -> ChronoUnit.MINUTES.between(t.getDataHora(), agora) <= 5)
                .toList();

        if (recentes.isEmpty()) {
            System.out.println("Nenhuma transação recente.");
            return;
        }

        for (int i = 0; i < recentes.size(); i++) {
            TransacaoPIX t = recentes.get(i);
            System.out.printf("%d. Para: %s | R$ %.2f\n   Mensagem: %s\n", i + 1, t.getDestinatario(), t.getValor(), t.getMensagem());
        }

        System.out.print("Deseja reembolsar alguma? (número ou 0): ");
        int escolha = scanner.nextInt();

        if (escolha > 0 && escolha <= recentes.size()) {
            TransacaoPIX t = recentes.get(escolha - 1);
            Usuario destinatario = usuarios.stream()
                    .filter(u -> u.getNome().equals(t.getDestinatario()))
                    .findFirst().orElse(null);

            if (destinatario != null && destinatario.getConta().sacar(t.getValor())) {
                usuario.getConta().depositar(t.getValor());
                System.out.println("Reembolso realizado com sucesso!");
            } else {
                System.out.println("Erro: destinatário sem saldo ou inexistente.");
            }
        }
    }
}
