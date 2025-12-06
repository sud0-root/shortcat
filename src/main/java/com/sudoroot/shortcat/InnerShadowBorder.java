package com.sudoroot.shortcat;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class InnerShadowBorder extends AbstractBorder {
    private final Insets insets = new Insets(1, 1, 1, 1);
    private final Color shadowColor;
    private final int shadowSize;

    public InnerShadowBorder(Color shadowColor, int shadowSize) {
        this.shadowColor = shadowColor;
        this.shadowSize = shadowSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(shadowColor);

        for (int i = 0; i < shadowSize; i++) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f - i * 0.03f));
            g2d.drawRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1);
        }

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}