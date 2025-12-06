package com.sudoroot.shortcat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShortcutRepository {

    public void addShortcut(ShortcutEntry entry) {
        String sql = "INSERT INTO shortcuts(id, command, keyword, expansion_text) VALUES(?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getId().toString());
            pstmt.setString(2, entry.getCommand());
            pstmt.setString(3, entry.getKeyword());
            pstmt.setString(4, entry.getExpansionText());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении шортката: " + e.getMessage());
        }
    }

    public List<ShortcutEntry> getAllShortcuts() {
        List<ShortcutEntry> list = new ArrayList<>();
        String sql = "SELECT id, command, keyword, expansion_text FROM shortcuts";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String command = rs.getString("command");
                String keyword = rs.getString("keyword");
                String expansion = rs.getString("expansion_text");

                list.add(new ShortcutEntry(id, command, keyword, expansion));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении шорткатов: " + e.getMessage());
        }
        return list;
    }

    public void updateShortcut(ShortcutEntry entry) {
        String sql = "UPDATE shortcuts SET command = ?, keyword = ?, expansion_text = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getCommand());
            pstmt.setString(2, entry.getKeyword());
            pstmt.setString(3, entry.getExpansionText());
            pstmt.setString(4, entry.getId().toString());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении шортката: " + e.getMessage());
        }
    }

    public void deleteShortcut(UUID id) {
        String sql = "DELETE FROM shortcuts WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении шортката: " + e.getMessage());
        }
    }
}