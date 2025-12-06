package com.sudoroot.shortcat;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private final Color startColor;
    private final Color endColor;

    public GradientPanel(LayoutManager layout, Color startColor, Color endColor) {
        super(layout);
        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}