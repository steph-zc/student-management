package com.studentmanagement;

import java.util.Scanner;

/**
 * Entrada de dados pelo teclado, com validacao.
 *
 * Concentrar a leitura aqui evita que o programa quebre quando o usuario digita
 * texto onde se espera numero.
 */
public final class Menu {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Menu() {
    }

    /** Le uma linha e encerra o programa com elegancia se a entrada acabar. */
    private static String nextLine() {
        if (!SCANNER.hasNextLine()) {
            System.out.println("\nEntrada encerrada.");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    /** Insiste ate receber um inteiro valido. */
    public static int readInt(String label) {
        while (true) {
            System.out.print(label);
            String input = nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Digite um numero inteiro.");
            }
        }
    }

    /** Insiste ate receber um texto nao vazio. */
    public static String readText(String label) {
        while (true) {
            System.out.print(label);
            String input = nextLine();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  Campo obrigatorio.");
        }
    }

    public static void printOptions() {
        System.out.println("""

                ==========================================
                        GERENCIAMENTO DE ALUNOS
                ==========================================
                 1 - Cadastrar curso
                 2 - Cadastrar aluno (em um ou mais cursos)
                 3 - Listar cursos
                 4 - Listar alunos com seus cursos
                 5 - Listar alunos de um curso
                 6 - Total de alunos por curso
                 7 - Buscar aluno por matricula
                 8 - Matricular aluno em outro curso
                 9 - Gerar base de dados (12 cursos + 5.000 alunos)
                 0 - Sair
                ==========================================""");
    }
}
