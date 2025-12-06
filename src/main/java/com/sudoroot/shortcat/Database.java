package com.sudoroot.shortcat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:db/app.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase() {
        String createShortcutsTable = """
                CREATE TABLE IF NOT EXISTS shortcuts (
                    id TEXT PRIMARY KEY,
                    command TEXT NOT NULL,
                    keyword TEXT NOT NULL,
                    expansion_text TEXT NOT NULL
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createShortcutsTable);
            System.out.println("База данных и таблица shortcuts инициализированы.");

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации базы данных:");
            e.printStackTrace();
        }
    }
}