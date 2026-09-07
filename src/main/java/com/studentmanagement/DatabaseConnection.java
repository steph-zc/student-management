package com.studentmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Ponto unico de conexao com o PostgreSQL. */
public final class DatabaseConnection {

    private static final Properties CONFIG = new Properties();
    private static boolean loaded = false;

    private DatabaseConnection() {
    }

    /**
     * Carrega o db.properties na primeira chamada.
     *
     * Nao fica em bloco static porque uma falha ali viraria
     * ExceptionInInitializerError, e o usuario receberia uma stack trace em vez
     * da instrucao do que fazer. A leitura e feita em UTF-8 para nao corromper
     * senhas com acento.
     */
    private static synchronized void loadConfig() {
        if (loaded) {
            return;
        }
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new IllegalStateException("""
                        Arquivo db.properties nao encontrado.
                        Rode:
                          cp src/main/resources/db.properties.example \
                        src/main/resources/db.properties
                        e informe a sua senha do PostgreSQL nesse arquivo.""");
            }
            CONFIG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            loaded = true;

        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler db.properties: " + e.getMessage());
        }
    }

    /** Abre uma conexao nova. Use sempre dentro de try-with-resources. */
    public static Connection getConnection() throws SQLException {
        loadConfig();
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    /** Traduz o codigo SQLSTATE do erro em uma instrucao que o usuario consegue seguir. */
    public static String describeError(SQLException e) {
        String sqlState = e.getSQLState() == null ? "" : e.getSQLState();
        return switch (sqlState) {
            case "28P01" -> "Senha incorreta. Revise o db.properties.";
            case "3D000" -> "Banco 'college' nao existe. Rode: createdb -U postgres college";
            case "42P01" -> "Tabela nao existe. Rode o sql/schema.sql no banco college.";
            case "23503" -> "O curso informado nao existe. Cadastre o curso antes.";
            case "23505" -> "Ja existe um aluno com essa matricula.";
            case "23514" -> "Valor invalido: a duracao do curso deve ser positiva.";
            case "08001", "08006" -> "Servidor PostgreSQL fora do ar ou porta errada (padrao 5432).";
            default -> "Erro de banco (" + sqlState + "): " + e.getMessage();
        };
    }

    /** Teste isolado da conexao, sem subir o menu. */
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Conectado a: " + connection.getCatalog());
            System.out.println("Servidor: " + connection.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("Falha na conexao: " + describeError(e));
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}