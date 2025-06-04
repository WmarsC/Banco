import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SistemaBanco {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("╔════════════════════════╗");
            System.out.println("║   BEM-VINDO AO BANCO   ║");
            System.out.println("╠════════════════════════╣");
            System.out.println("║ 1. Criar conta         ║");
            System.out.println("║ 2. Login               ║");
            System.out.println("║ 3. Suporte             ║");
            System.out.println("║ 4. Sair                ║");
            System.out.println("╚════════════════════════╝");
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
        scanner.nextLine(); // Limpa buffer
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.println("Tipo de conta: 1. Corrente | 2. Poupança");
        int tipo = scanner.nextInt();

        Conta conta;
        String numero = UUID.randomUUID().toString().substring(0, 8);

        if (tipo == 1) {
            conta = new ContaCorrente(numero, nome);
        } else {
            conta = new ContaPoupanca(numero, nome);
        }

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
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            System.out.println("Credenciais inválidas!");
            return;
        }

        menuConta(usuario);
    }

    private static void menuConta(Usuario usuario) {
        int opcao;
        do {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.printf ("║ Olá, %-25s║\n", usuario.getNome());
            System.out.println("╠══════════════════════════════╣");
            System.out.printf ("║ Tipo de conta: %-14s║\n", usuario.getConta().getTipoConta());
            System.out.printf ("║ Saldo: R$ %-19.2f║\n", usuario.getConta().getSaldo());
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. Depositar                 ║");
            System.out.println("║ 2. Sacar                     ║"); // NOVA OPÇÃO
            System.out.println("║ 3. Fazer PIX                 ║");
            System.out.println("║ 4. Extrato PIX (últimos 5m) ║"); // RENOMEADA
            System.out.println("║ 5. Sair                      ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> depositar(usuario);
                case 2 -> sacar(usuario); // NOVA FUNÇÃO
                case 3 -> fazerPix(usuario);
                case 4 -> mostrarExtratoPix(usuario);
                case 5 -> System.out.println("Saindo da conta...");
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 5);
    }

    private static void depositar(Usuario usuario) {
        System.out.print("Valor para depositar: ");
        double valor = scanner.nextDouble();
        usuario.getConta().depositar(valor);
        System.out.println("Depósito realizado com sucesso!");
    }

    private static void sacar(Usuario usuario) {
        System.out.print("Valor para sacar: ");
        double valor = scanner.nextDouble();
        if (usuario.getConta().sacar(valor)) {
            System.out.println("Saque realizado com sucesso!");
        } else {
            System.out.println("Saldo insuficiente.");
        }
    }

    private static void fazerPix(Usuario remetente) {
        scanner.nextLine();
        System.out.print("Digite o nome do destinatário: ");
        String nomeDestinatario = scanner.nextLine();

        Usuario destinatario = usuarios.stream()
                .filter(u -> u.getNome().equals(nomeDestinatario))
                .findFirst()
                .orElse(null);

        if (destinatario == null) {
            System.out.println("Destinatário não encontrado.");
            return;
        }

        System.out.print("Valor do PIX: ");
        double valor = scanner.nextDouble();

        if (!remetente.getConta().sacar(valor)) {
            System.out.println("Saldo insuficiente.");
            return;
        }

        destinatario.getConta().depositar(valor);

        TransacaoPIX transacao = new TransacaoPIX(destinatario.getNome(), valor);
        remetente.getConta().adicionarTransacaoPix(transacao);

        System.out.println("PIX enviado com sucesso!");
    }

    private static void mostrarExtratoPix(Usuario usuario) {
        List<TransacaoPIX> historico = usuario.getConta().getHistoricoPix();
        LocalDateTime agora = LocalDateTime.now();

        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║  EXTRATO DE PIX - Últimos 5 Min   ║");
        System.out.println("╠════════════════════════════════════╣");

        List<TransacaoPIX> recentes = historico.stream()
                .filter(t -> ChronoUnit.MINUTES.between(t.getDataHora(), agora) <= 5)
                .toList();

        if (recentes.isEmpty()) {
            System.out.println("║ Nenhuma transação recente         ║");
        } else {
            int i = 1;
            for (TransacaoPIX t : recentes) {
                System.out.printf("║ %d. Para: %-10s | R$ %.2f     ║\n", i++, t.getDestinatario(), t.getValor());
            }

            System.out.print("╠════════════════════════════════════╣\n");
            System.out.print("Deseja reembolsar alguma? (número ou 0 para sair): ");
            int escolha = scanner.nextInt();

            if (escolha > 0 && escolha <= recentes.size()) {
                TransacaoPIX selecionada = recentes.get(escolha - 1);

                Usuario destinatario = usuarios.stream()
                        .filter(u -> u.getNome().equals(selecionada.getDestinatario()))
                        .findFirst()
                        .orElse(null);

                if (destinatario != null && destinatario.getConta().sacar(selecionada.getValor())) {
                    usuario.getConta().depositar(selecionada.getValor());
                    System.out.println("Reembolso realizado com sucesso!");
                } else {
                    System.out.println("Erro no reembolso: destinatário não encontrado ou saldo insuficiente.");
                }
            }
        }
        System.out.println("╚════════════════════════════════════╝");
    }
}
