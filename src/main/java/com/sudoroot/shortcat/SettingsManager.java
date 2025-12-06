package com.sudoroot.shortcat;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsManager {

    private static final Path SETTINGS_FILE_PATH = Paths.get(System.getProperty("user.home"), ".shortcat", "settings.properties");
    private final Properties properties;

    public static final String KEY_CLEAR_BUFFER_MODIFIERS = "hotkey.clear.buffer.modifiers";
    public static final String KEY_CLEAR_BUFFER_CODE = "hotkey.clear.buffer.code";

    public SettingsManager() {
        this.properties = new Properties();
        loadSettings();
    }

    private void loadSettings() {
        properties.setProperty(KEY_CLEAR_BUFFER_MODIFIERS, "0");
        properties.setProperty(KEY_CLEAR_BUFFER_CODE, String.valueOf(NativeKeyEvent.VC_ESCAPE));

        if (Files.exists(SETTINGS_FILE_PATH)) {
            try (InputStream input = new FileInputStream(SETTINGS_FILE_PATH.toFile())) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке настроек: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void saveSettings() {
        try {
            Files.createDirectories(SETTINGS_FILE_PATH.getParent());
            try (OutputStream output = new FileOutputStream(SETTINGS_FILE_PATH.toFile())) {
                properties.store(output, "ShortCAT Application Settings");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении настроек: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getClearBufferModifiers() {
        try {
            return Integer.parseInt(properties.getProperty(KEY_CLEAR_BUFFER_MODIFIERS));
        } catch (NumberFormatException e) {
            System.err.println("Неверный код модификаторов в файле настроек, используется значение по умолчанию.");
            return 0; // 0 = нет модификаторов
        }
    }

    public int getClearBufferKeyCode() {
        try {
            return Integer.parseInt(properties.getProperty(KEY_CLEAR_BUFFER_CODE));
        } catch (NumberFormatException e) {
            System.err.println("Неверный код клавиши в файле настроек, используется значение по умолчанию.");
            return NativeKeyEvent.VC_ESCAPE;
        }
    }

    public void setClearBufferHotKey(int modifiers, int keyCode) {
        properties.setProperty(KEY_CLEAR_BUFFER_MODIFIERS, String.valueOf(modifiers));
        properties.setProperty(KEY_CLEAR_BUFFER_CODE, String.valueOf(keyCode));
        saveSettings();
    }
}