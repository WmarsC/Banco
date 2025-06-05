import java.util.Scanner;

public class Suporte {
    private static final Scanner scanner = new Scanner(System.in);

    public static void menuSuporte() {
        System.out.println("\u2554\u2550\u2550\u2550\u2550 MENU DE SUPORTE \u2550\u2550\u2550\u2550");
        System.out.println("1. Como criar uma conta?");
        System.out.println("2. Como fazer um PIX?");
        System.out.println("3. Contatar atendimento");
        System.out.println("4. Voltar");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        switch (opcao) {
            case 1 -> System.out.println("Vá ao menu principal e selecione 'Criar conta'.");
            case 2 -> System.out.println("Após o login, selecione 'Fazer PIX'.");
            case 3 -> System.out.println("Atendimento: suporte@banco.com");
            case 4 -> System.out.println("Voltando...");
            default -> System.out.println("Opção inválida.");
        }
    }
}
