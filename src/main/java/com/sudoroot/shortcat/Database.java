package com.sudoroot.shortcat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // 1. Получаем путь к папке пользователя (C:\Users\Name)
    private static final String USER_HOME = System.getProperty("user.home");

    // 2. Определяем рабочую папку приложения (.shortcat)
    private static final File APP_DIR = new File(USER_HOME, ".shortcat");

    // 3. Полный путь к файлу БД
    private static final String DB_URL = "jdbc:sqlite:" + new File(APP_DIR, "app.db").getAbsolutePath();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase() {
        // --- ЭТАП 1: Создание папки в User Home ---
        // Если папки C:\Users\User\.shortcat нет — создаем её.
        // Там есть права на запись всегда!
        if (!APP_DIR.exists()) {
            boolean created = APP_DIR.mkdirs();
            if (created) {
                System.out.println("Рабочая директория создана: " + APP_DIR.getAbsolutePath());
            } else {
                System.err.println("Не удалось создать директорию: " + APP_DIR.getAbsolutePath());
            }
        }

        // --- ЭТАП 2: Создание таблицы ---
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
            System.out.println("База данных подключена: " + DB_URL);

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации базы данных:");
            e.printStackTrace();
        }
    }
}