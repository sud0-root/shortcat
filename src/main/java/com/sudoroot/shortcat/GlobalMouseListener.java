package com.sudoroot.shortcat;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

public class GlobalMouseListener implements NativeMouseListener {

    private final GlobalKeyListener globalKeyListener;

    public GlobalMouseListener(GlobalKeyListener globalKeyListener) {
        this.globalKeyListener = globalKeyListener;
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        globalKeyListener.resetBuffer();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
    }
}