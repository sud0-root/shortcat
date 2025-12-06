package com.sudoroot.shortcat;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

public class TextExpander {

    private final Robot robot;

    public TextExpander() throws AWTException {
        this.robot = new Robot();
        robot.setAutoDelay(40);
    }

    public void performExpansion(String shortcutText, String expansionText) {
        int currentAutoDelay = robot.getAutoDelay();
        robot.setAutoDelay(1);

        for (int i = 0; i < shortcutText.length(); i++) {
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        }

        robot.setAutoDelay(currentAutoDelay);

        pasteText(expansionText);
    }

    private void pasteText(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable oldClipboardContent = clipboard.getContents(null);

        try {
            StringSelection stringSelection = new StringSelection(text);
            clipboard.setContents(stringSelection, stringSelection);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);

        } finally {
            robot.delay(100);
            clipboard.setContents(oldClipboardContent, null);
        }
    }
}