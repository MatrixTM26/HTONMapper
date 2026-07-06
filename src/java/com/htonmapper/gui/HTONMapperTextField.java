package com.htonmapper.gui;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class HTONMapperTextField extends JTextField {

    public HTONMapperTextField(String DefaultValue) {
        super(DefaultValue);
        ApplyBaseStyle();
        AttachFocusBehavior();
    }

    private void ApplyBaseStyle() {
        setFont(HTONMapperTheme.FontMono);
        setBackground(HTONMapperTheme.ColorBackgroundInset);
        setForeground(HTONMapperTheme.ColorTextPrimary);
        setCaretColor(HTONMapperTheme.ColorSoftBlue);
        setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
        setMargin(new java.awt.Insets(5, 8, 5, 8));
    }

    private void AttachFocusBehavior() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent EventArg) {
                setBorder(new LineBorder(HTONMapperTheme.ColorSoftBlue, 1));
            }

            @Override
            public void focusLost(FocusEvent EventArg) {
                setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
            }
        });
    }
}
