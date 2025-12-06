package com.sudoroot.shortcat;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class ConfigurationFrame extends JFrame {

    private final ShortcutManager shortcutManager;
    private final JTable shortcutTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    public ConfigurationFrame(ShortcutManager shortcutManager) {
        this.shortcutManager = shortcutManager;

        setTitle("Настройка ShortCAT");
        setIconImage(Main.createImage("/images/tray_icon.png", "app icon"));
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                Random random = new Random(0);
                Color noiseColor = new Color(0, 0, 0, 10);
                g2d.setColor(noiseColor);
                for (int i = 0; i < getWidth() * getHeight() * 0.1; i++) {
                    int x = random.nextInt(getWidth());
                    int y = random.nextInt(getHeight());
                    g2d.fillRect(x, y, 1, 1);
                }
                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Команда", "Ключевое слово", "Текст расширения"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        shortcutTable = new JTable(tableModel);

        shortcutTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shortcutTable.setRowHeight(28);
        shortcutTable.setFillsViewportHeight(true);
        shortcutTable.putClientProperty("JTable.alternateRowColor", true);
        shortcutTable.getTableHeader().setFont(shortcutTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        shortcutTable.setAutoCreateRowSorter(true);
        setTableColumnWidths();

        JScrollPane scrollPane = new JScrollPane(shortcutTable);
        scrollPane.setBorder(new CompoundBorder(
                new EmptyBorder(5, 0, 0, 0),
                new InnerShadowBorder(UIManager.getColor("innerShadowColor"), 5)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new GradientPanel(new BorderLayout(),
                UIManager.getColor("panelBackgroundColor"),
                UIManager.getColor("panelGradientEndColor"));

        statusLabel = new JLabel("Всего ярлыков: 0");
        statusLabel.setForeground(UIManager.getColor("foregroundColor"));
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        footerPanel.add(statusLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Добавить", IconLoader.load("add.svg"));
        JButton editButton = new JButton("Изменить", IconLoader.load("edit.svg"));
        JButton deleteButton = new JButton("Удалить", IconLoader.load("delete.svg"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        addButton.addActionListener(e -> addShortcutAction());
        editButton.addActionListener(e -> editShortcutAction());
        deleteButton.addActionListener(e -> deleteShortcutAction());

        refreshTable();
    }
    
    public void refreshTable() {
        int selectedRow = shortcutTable.getSelectedRow();
        tableModel.setRowCount(0);
        List<ShortcutEntry> shortcuts = shortcutManager.getAllShortcuts();
        for (ShortcutEntry entry : shortcuts) {
            tableModel.addRow(new Object[]{entry.getCommand(), entry.getKeyword(), entry.getExpansionText()});
        }
        if (selectedRow >= 0 && selectedRow < shortcutTable.getRowCount()) {
            shortcutTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        statusLabel.setText("Всего ярлыков: " + shortcuts.size());
    }

    private void setTableColumnWidths() {
        TableColumn column;
        column = shortcutTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(100);
        column.setMaxWidth(150);
        column = shortcutTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);
        column.setMaxWidth(200);
        column = shortcutTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(450);
    }

    private void addShortcutAction() {
        ShortcutDialog.showDialog(this, "Добавить новый ярлык", shortcutManager, null)
                .ifPresent(newEntry -> {
                    shortcutManager.addShortcut(newEntry);
                    refreshTable();
                    shortcutTable.setRowSelectionInterval(shortcutTable.getRowCount() - 1, shortcutTable.getRowCount() - 1);
                });
    }

    private void editShortcutAction() {
        int selectedRow = shortcutTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите ярлык для редактирования.", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = shortcutTable.convertRowIndexToModel(selectedRow);
        ShortcutEntry entryToEdit = shortcutManager.getAllShortcuts().get(modelRow);
        ShortcutDialog.showDialog(this, "Редактировать ярлык", shortcutManager, entryToEdit)
                .ifPresent(updatedEntry -> {
                    shortcutManager.updateShortcut(updatedEntry);
                    refreshTable();
                });
    }

    private void deleteShortcutAction() {
        int selectedRow = shortcutTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите ярлык для удаления.", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = shortcutTable.convertRowIndexToModel(selectedRow);
        ShortcutEntry entryToDelete = shortcutManager.getAllShortcuts().get(modelRow);
        int choice = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить ярлык \"" + entryToDelete.getFullShortcutText() + "\"?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            shortcutManager.deleteShortcut(entryToDelete.getId());
            refreshTable();
        }
    }
}