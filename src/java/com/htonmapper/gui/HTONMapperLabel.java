package com.htonmapper.gui;

import java.awt.Color;
import javax.swing.JLabel;

public class HTONMapperLabel extends JLabel {

    public HTONMapperLabel(String LabelText) {
        super(LabelText);
        setFont(HTONMapperTheme.FontMono);
        setForeground(HTONMapperTheme.ColorTextSecondary);
    }

    public HTONMapperLabel(String LabelText, Color ForegroundColorArg) {
        super(LabelText);
        setFont(HTONMapperTheme.FontMono);
        setForeground(ForegroundColorArg);
    }

    public HTONMapperLabel SetBoldStyle() {
        setFont(HTONMapperTheme.FontMonoBold);
        return this;
    }

    public HTONMapperLabel SetHeadingStyle() {
        setFont(HTONMapperTheme.FontMonoHeading);
        setForeground(HTONMapperTheme.ColorTextPrimary);
        return this;
    }

    public HTONMapperLabel SetSmallStyle() {
        setFont(HTONMapperTheme.FontMonoSmall);
        setForeground(HTONMapperTheme.ColorTextMuted);
        return this;
    }
}
