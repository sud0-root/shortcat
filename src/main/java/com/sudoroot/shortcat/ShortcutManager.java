package com.sudoroot.shortcat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ShortcutManager {


    private final List<ShortcutEntry> shortcuts;
    private static final Path SAVE_FILE_PATH = Paths.get(System.getProperty("user.home"), ".shortcat", "shortcuts.dat");

    public ShortcutManager() {
        this.shortcuts = new CopyOnWriteArrayList<>(loadShortcuts());
    }

    public void addShortcut(ShortcutEntry entry) {
        shortcuts.add(entry);
        saveShortcuts();
    }

    public void updateShortcut(ShortcutEntry updatedEntry) {
        for (int i = 0; i < shortcuts.size(); i++) {
            if (shortcuts.get(i).getId().equals(updatedEntry.getId())) {
                shortcuts.set(i, updatedEntry);
                saveShortcuts();
                return;
            }
        }
    }

    public void deleteShortcut(UUID id) {
        shortcuts.removeIf(entry -> entry.getId().equals(id));
        saveShortcuts();
    }

    public boolean isDuplicateExists(ShortcutEntry entryToCheck) {
        String fullShortcutText = entryToCheck.getFullShortcutText().toLowerCase();

        return shortcuts.stream()
                .anyMatch(existingEntry ->
                        !existingEntry.getId().equals(entryToCheck.getId()) &&
                                existingEntry.getFullShortcutText().equalsIgnoreCase(fullShortcutText)
                );
    }

    public List<ShortcutEntry> getAllShortcuts() {
        return Collections.unmodifiableList(shortcuts.stream().collect(Collectors.toList()));
    }

    public ShortcutEntry findExpansion(String textToMatch) {
        if (textToMatch == null || textToMatch.isEmpty()) {
            return null;
        }
        return shortcuts.stream()
                .filter(entry -> textToMatch.endsWith(entry.getFullShortcutText()))
                .findFirst()
                .orElse(null);
    }

    private void saveShortcuts() {
        try {
            Files.createDirectories(SAVE_FILE_PATH.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH.toFile()))) {
                oos.writeObject(shortcuts);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении ярлыков: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<ShortcutEntry> loadShortcuts() {
        if (Files.exists(SAVE_FILE_PATH)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_PATH.toFile()))) {
                return (List<ShortcutEntry>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при загрузке ярлыков: " + e.getMessage());
            }
        }
        return createDefaultShortcuts();
    }

    private List<ShortcutEntry> createDefaultShortcuts() {
        CopyOnWriteArrayList<ShortcutEntry> defaultList = new CopyOnWriteArrayList<>();
        defaultList.add(new ShortcutEntry(UUID.randomUUID(), "!sig", "mysig", "С наилучшими пожеланиями,\nИван Петров"));
        defaultList.add(new ShortcutEntry(UUID.randomUUID(), "!adr", "work", "ул. Ленина, д. 1, офис 101, г. Москва, 101000"));
        return defaultList;
    }
}