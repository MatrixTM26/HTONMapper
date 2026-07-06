package com.htonmapper.gui;

import javax.swing.JFrame;

public class HTONMapperFrame extends JFrame {

    public HTONMapperFrame(String TitleText) {
        super(TitleText);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new java.awt.Dimension(760, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(HTONMapperTheme.ColorBackgroundCanvas);
    }
}
