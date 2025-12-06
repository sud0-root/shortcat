package com.sudoroot.shortcat;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static ConfigurationFrame configFrame;
    private static ShortcutManager shortcutManager;

    public static void main(String[] args) {
        try {
            FlatPropertiesLaf pastelLaf = new FlatPropertiesLaf("Solarized Pastel", Main.class.getResourceAsStream("/SolarizedLightPastelTheme.properties"));
            UIManager.setLookAndFeel(pastelLaf);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить кастомную тему. Используется тема по умолчанию.");
            FlatLightLaf.setup();
        }

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);

        shortcutManager = new ShortcutManager();

        GlobalKeyListener keyListener = new GlobalKeyListener(shortcutManager);
        GlobalMouseListener mouseListener = new GlobalMouseListener(keyListener);

        SwingUtilities.invokeLater(Main::setupSystemTray);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("Критическая ошибка: Не удалось зарегистрировать Native Hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
    }

    private static void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            showConfigurationFrame();
            return;
        }

        PopupMenu popup = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(createImage("/images/tray_icon.png", "tray icon"));
        SystemTray tray = SystemTray.getSystemTray();

        MenuItem configureItem = new MenuItem("Настроить ShortCAT");
        MenuItem exitItem = new MenuItem("Выход");

        popup.add(configureItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("ShortCAT - Текстовый расширитель");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("Не удалось добавить иконку в системный трей.");
        }

        trayIcon.addActionListener(e -> showConfigurationFrame());

        configureItem.addActionListener(e -> showConfigurationFrame());

        exitItem.addActionListener(e -> {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                ex.printStackTrace();
            }
            tray.remove(trayIcon);
            System.exit(0);
        });
    }

    private static void showConfigurationFrame() {
        if (configFrame == null) {
            configFrame = new ConfigurationFrame(shortcutManager);
        }
        if ((configFrame.getExtendedState() & Frame.ICONIFIED) != 0) {
            configFrame.setExtendedState(Frame.NORMAL);
        }
        configFrame.setVisible(true);
        configFrame.toFront();
        configFrame.requestFocus();
    }

    protected static Image createImage(String path, String description) {
        URL imageURL = Main.class.getResource(path);
        if (imageURL == null) {
            System.err.println("Ресурс не найден: " + path);
            return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}