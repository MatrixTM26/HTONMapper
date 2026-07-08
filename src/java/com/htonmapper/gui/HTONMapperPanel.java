package com.htonmapper.gui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class HTONMapperPanel extends JPanel {

    public HTONMapperPanel() {
        super();
        setBackground(HTONMapperTheme.ColorBackgroundPanel);
    }

    public HTONMapperPanel(LayoutManager LayoutArg) {
        super(LayoutArg);
        setBackground(HTONMapperTheme.ColorBackgroundPanel);
    }

    public HTONMapperPanel SetCardStyle() {
        setBorder(new CompoundBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 2), new EmptyBorder(14, 16, 14, 16)));
        return this;
    }

    public HTONMapperPanel SetCanvasBackground() {
        setBackground(HTONMapperTheme.ColorBackgroundCanvas);
        return this;
    }

    public HTONMapperPanel SetPaddedInset(int TopArg, int LeftArg, int BottomArg, int RightArg) {
        setBorder(new EmptyBorder(TopArg, LeftArg, BottomArg, RightArg));
        return this;
    }
}
