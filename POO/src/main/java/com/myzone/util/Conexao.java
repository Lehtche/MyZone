package com.myzone.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String NOMEBANCO = "MyZone";
    private static final String URL = "jdbc:mysql://localhost:3306/" + NOMEBANCO;
    private static final String USUARIO = "root";
    private static final String SENHA = "QWERqwer132";

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
