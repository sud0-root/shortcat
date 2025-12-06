package com.sudoroot.shortcat;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;

public class IconLoader {
    public static Icon load(String name) {
        return new FlatSVGIcon("icons/" + name, 16, 16);
    }
}