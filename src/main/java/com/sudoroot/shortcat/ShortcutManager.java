package com.sudoroot.shortcat;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ShortcutManager {

    private final List<ShortcutEntry> shortcuts;
    private final ShortcutRepository repository;

    public ShortcutManager() {
        this.repository = new ShortcutRepository();

        this.shortcuts = new CopyOnWriteArrayList<>(repository.getAllShortcuts());

        if (shortcuts.isEmpty()) {
            createDefaultShortcuts();
        }
    }

    public void addShortcut(ShortcutEntry entry) {
        shortcuts.add(entry);
        repository.addShortcut(entry);
    }

    public void updateShortcut(ShortcutEntry updatedEntry) {
        for (int i = 0; i < shortcuts.size(); i++) {
            if (shortcuts.get(i).getId().equals(updatedEntry.getId())) {
                shortcuts.set(i, updatedEntry);
                repository.updateShortcut(updatedEntry);
                return;
            }
        }
    }

    public void deleteShortcut(UUID id) {
        shortcuts.removeIf(entry -> entry.getId().equals(id));
        repository.deleteShortcut(id);
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

    private void createDefaultShortcuts() {
        ShortcutEntry example = new ShortcutEntry(UUID.randomUUID(), "!sig", "mysig", "С наилучшими пожеланиями,\nИван Петров");
        addShortcut(example);
    }
}