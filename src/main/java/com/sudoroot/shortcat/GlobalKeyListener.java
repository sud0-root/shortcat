package com.sudoroot.shortcat;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;

public class GlobalKeyListener implements NativeKeyListener {

    private final StringBuilder typedBuffer = new StringBuilder();
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
        if (typedBuffer.length() > 0) {
            typedBuffer.setLength(0);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        if (isExpanding) return;

        char keyChar = e.getKeyChar();

        if (!Character.isISOControl(keyChar)) {
            typedBuffer.append(keyChar);
        } else {
            resetBuffer();
            return;
        }

        if (typedBuffer.length() > BUFFER_LIMIT) {
            typedBuffer.delete(0, typedBuffer.length() - BUFFER_LIMIT);
        }

        ShortcutEntry match = shortcutManager.findExpansion(typedBuffer.toString().toLowerCase());

        if (match != null) {
            final String fullShortcut = match.getFullShortcutText();
            final String expansion = match.getExpansionText();

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
            if (typedBuffer.length() > 0) {
                typedBuffer.setLength(typedBuffer.length() - 1);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }
}