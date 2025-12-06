package com.sudoroot.shortcat;

import java.io.Serializable;
import java.util.UUID;

public class ShortcutEntry implements Serializable {

    private final UUID id;
    private String command;
    private String keyword;
    private String expansionText;

    public ShortcutEntry(UUID id, String command, String keyword, String expansionText) {
        if (id == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }
        this.id = id;
        this.command = command;
        this.keyword = keyword;
        this.expansionText = expansionText;
    }

    public UUID getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getExpansionText() {
        return expansionText;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setExpansionText(String expansionText) {
        this.expansionText = expansionText;
    }

    public String getFullShortcutText() {
        return keyword + command;
    }
}