package com.sudoroot.shortcat;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static ConfigurationFrame configFrame;
    private static ShortcutManager shortcutManager;

    // --- ДЛЯ ЗАЩИТЫ ОТ ПОВТОРНОГО ЗАПУСКА ---
    // Уникальный порт. Если он занят - значит программа уже запущена.
    private static final int SINGLE_INSTANCE_PORT = 64751;
    private static ServerSocket uniqueInstanceSocket;

    public static void main(String[] args) {
        // 1. ПРОВЕРКА НА ЕДИНСТВЕННЫЙ ЭКЗЕМПЛЯР
        // Это должно быть самой первой строчкой.
        if (!checkSingleInstance()) {
            System.err.println("Приложение ShortCAT уже запущено.");
            // Молча выходим, пользователь просто увидит, что второе окно не открылось
            System.exit(0);
        }

        // 2. Настройка темы
        try {
            FlatPropertiesLaf pastelLaf = new FlatPropertiesLaf("Solarized Pastel", Main.class.getResourceAsStream("/SolarizedLightPastelTheme.properties"));
            UIManager.setLookAndFeel(pastelLaf);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить кастомную тему. Используется тема по умолчанию.");
            FlatLightLaf.setup();
        }

        // 3. Инициализация Базы Данных
        // Критически важно создать папку и таблицы до старта логики
        Database.initDatabase();

        // 4. Отключение лишних логов (JNativeHook очень шумный)
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);

        // 5. Запуск логики
        shortcutManager = new ShortcutManager();

        GlobalKeyListener keyListener = new GlobalKeyListener(shortcutManager);
        GlobalMouseListener mouseListener = new GlobalMouseListener(keyListener);

        // 6. Запуск UI (Трей и Окна)
        SwingUtilities.invokeLater(Main::setupSystemTray);

        // 7. Регистрация глобальных хуков
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(keyListener);
            GlobalScreen.addNativeMouseListener(mouseListener);
        } catch (NativeHookException ex) {
            System.err.println("Критическая ошибка: Не удалось зарегистрировать Native Hook.");
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, "Ошибка запуска перехватчика клавиш!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Пытается открыть серверный сокет на localhost.
     * Если не получается (порт занят) - значит копия программы уже работает.
     */
    private static boolean checkSingleInstance() {
        try {
            uniqueInstanceSocket = new ServerSocket(SINGLE_INSTANCE_PORT, 0, InetAddress.getByAddress(new byte[] {127, 0, 0, 1}));
            return true;
        } catch (IOException e) {
            return false;
        }
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

        // Жирный шрифт для главного пункта (только для эстетики, если поддерживается)
        configureItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

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

        // Двойной клик - настройки
        trayIcon.addActionListener(e -> showConfigurationFrame());

        // Пункт меню - настройки
        configureItem.addActionListener(e -> showConfigurationFrame());

        // ЛОГИКА ВЫХОДА
        exitItem.addActionListener(e -> shutdownApp(tray, trayIcon));
    }

    /**
     * Корректное завершение работы приложения.
     */
    private static void shutdownApp(SystemTray tray, TrayIcon trayIcon) {
        new Thread(() -> {
            try {
                // 1. Убираем иконку, чтобы она не висела "призраком"
                tray.remove(trayIcon);

                // 2. Отключаем хук
                // Это самое опасное место. Библиотеке нужно время.
                GlobalScreen.unregisterNativeHook();

                // --- МАГИЧЕСКОЕ ИСПРАВЛЕНИЕ ---
                // Даем нативному потоку (C++) 300мс на корректное отключение от Windows.
                // Без этой паузы System.exit() убивает процесс слишком рано, вызывая краш.
                Thread.sleep(300);

                // 3. Закрываем сокет
                if (uniqueInstanceSocket != null && !uniqueInstanceSocket.isClosed()) {
                    uniqueInstanceSocket.close();
                }
            } catch (Exception e) {
                // Если что-то пошло не так - не страшно, мы все равно выходим
                e.printStackTrace();
            } finally {
                // 4. Жесткий выход.
                // Используем halt, если exit все равно вызывает ошибку, но обычно exit(0) достаточно после паузы.
                System.exit(0);
            }
        }).start();
    }

    private static void showConfigurationFrame() {
        if (configFrame == null) {
            configFrame = new ConfigurationFrame(shortcutManager);
        }
        // Если свернуто - развернуть
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
            // Возвращаем пустую картинку 1x1, чтобы не падать
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}