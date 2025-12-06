package com.sudoroot.shortcat;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;

public class GlobalKeyListener implements NativeKeyListener {

    private final StringBuilder inputBuffer = new StringBuilder(); // RENAME: typedBuffer -> inputBuffer
    private final ShortcutManager shortcutManager;
    private final TextExpander textExpander;
    private static final int BUFFER_LIMIT = 100;
    private volatile static boolean isExpanding = false;

    public GlobalKeyListener(ShortcutManager shortcutManager) {
        this.shortcutManager = shortcutManager;
        try {
            this.textExpander = new TextExpander();
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void resetBuffer() {
        if (inputBuffer.length() > 0) {
            inputBuffer.setLength(0);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        if (isExpanding) return;

        char keyChar = e.getKeyChar();

        if (!Character.isISOControl(keyChar)) {
            inputBuffer.append(keyChar);
        } else {
            resetBuffer();
            return;
        }

        if (inputBuffer.length() > BUFFER_LIMIT) {
            inputBuffer.delete(0, inputBuffer.length() - BUFFER_LIMIT);
        }

        // rename: match -> detectedShortcut
        ShortcutEntry detectedShortcut = shortcutManager.findExpansion(inputBuffer.toString().toLowerCase());

        if (detectedShortcut != null) {
            final String fullShortcut = detectedShortcut.getFullShortcutText();
            final String expansion = detectedShortcut.getExpansionText();

            new Thread(() -> {
                try {
                    isExpanding = true;
                    textExpander.performExpansion(fullShortcut, expansion);
                } finally {
                    isExpanding = false;
                }
            }).start();

            resetBuffer();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (isExpanding) return;

        int code = e.getKeyCode();
        if (code == NativeKeyEvent.VC_LEFT ||
                code == NativeKeyEvent.VC_RIGHT ||
                code == NativeKeyEvent.VC_UP ||
                code == NativeKeyEvent.VC_DOWN ||
                code == NativeKeyEvent.VC_HOME ||
                code == NativeKeyEvent.VC_END ||
                code == NativeKeyEvent.VC_PAGE_UP ||
                code == NativeKeyEvent.VC_PAGE_DOWN) {

            resetBuffer();
            return;
        }

        if (code == NativeKeyEvent.VC_BACKSPACE) {
            if (inputBuffer.length() > 0) {
                inputBuffer.setLength(inputBuffer.length() - 1);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }
}