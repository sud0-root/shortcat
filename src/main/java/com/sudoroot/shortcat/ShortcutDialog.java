package com.sudoroot.shortcat;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class ShortcutDialog extends JDialog {

    private final JTextField commandField = new JTextField(20);
    private final JTextField keywordField = new JTextField(20);
    private final JTextArea expansionArea = new JTextArea(5, 20);

    private final ShortcutManager shortcutManager;
    private final ShortcutEntry entryBeingEdited;

    private ShortcutEntry result = null;

    public ShortcutDialog(Frame owner, String title, ShortcutManager manager, ShortcutEntry entryToEdit) {
        super(owner, title, true);

        this.shortcutManager = manager;
        this.entryBeingEdited = entryToEdit;

        Color borderColor = UIManager.getColor("borderColor");
        Border customBorder = BorderFactory.createLineBorder(borderColor, 1);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Команда (например, !sig):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        commandField.setBorder(customBorder);
        panel.add(commandField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Ключевое слово (например, mysig):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        keywordField.setBorder(customBorder);
        panel.add(keywordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Текст для вставки:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        expansionArea.setLineWrap(true); expansionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(expansionArea);
        scrollPane.setBorder(customBorder);
        panel.add(scrollPane, gbc);

        if (entryToEdit != null) {
            commandField.setText(entryToEdit.getCommand());
            keywordField.setText(entryToEdit.getKeyword());
            expansionArea.setText(entryToEdit.getExpansionText());
        }

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        okButton.addActionListener(e -> onOK());

        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        // Обработчик теперь только вызывает метод логики
        try {
            result = createValidEntry();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Вся валидация и создание объекта вынесены из обработчика
    private ShortcutEntry createValidEntry() {
        if (commandField.getText().trim().isEmpty() || keywordField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Команда и ключевое слово не могут быть пустыми.");
        }

        UUID id = (entryBeingEdited != null) ? entryBeingEdited.getId() : UUID.randomUUID();
        ShortcutEntry candidate = new ShortcutEntry(id, commandField.getText().trim(), keywordField.getText().trim(), expansionArea.getText());

        if (shortcutManager.isDuplicateExists(candidate)) {
            throw new IllegalArgumentException("Ярлык с такой комбинацией команды и ключевого слова уже существует.");
        }

        return candidate;
    }

    public static Optional<ShortcutEntry> showDialog(Frame owner, String title, ShortcutManager manager, ShortcutEntry entry) {
        ShortcutDialog dialog = new ShortcutDialog(owner, title, manager, entry);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.result);
    }
}