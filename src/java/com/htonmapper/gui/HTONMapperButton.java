package com.htonmapper.gui;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class HTONMapperButton extends JButton {

    public enum ButtonVariant {
        Default,
        Primary,
        Danger
    }

    private final ButtonVariant VariantType;

    public HTONMapperButton(String LabelText) {
        this(LabelText, ButtonVariant.Default);
    }

    public HTONMapperButton(String LabelText, ButtonVariant VariantTypeArg) {
        super(LabelText);
        this.VariantType = VariantTypeArg;
        ApplyBaseStyle();
        AttachHoverBehavior();
    }

    private void ApplyBaseStyle() {
        setFont(HTONMapperTheme.FontMonoBold);
        setFocusPainted(false);
        setOpaque(true);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new java.awt.Insets(6, 16, 6, 16));
        ApplyRestingColors();
    }

    private void ApplyRestingColors() {
        switch (VariantType) {
            case Primary:
                setBackground(HTONMapperTheme.ColorSoftGreenDim);
                setForeground(HTONMapperTheme.ColorTextPrimary);
                setBorder(new LineBorder(HTONMapperTheme.ColorSoftGreen, 1));
                break;
            case Danger:
                setBackground(HTONMapperTheme.ColorBackgroundPanel);
                setForeground(HTONMapperTheme.ColorSoftRed);
                setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
                break;
            default:
                setBackground(HTONMapperTheme.ColorBackgroundPanel);
                setForeground(HTONMapperTheme.ColorTextPrimary);
                setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
                break;
        }
    }

    private void ApplyHoverColors() {
        switch (VariantType) {
            case Primary:
                setBackground(HTONMapperTheme.ColorSoftGreen);
                setBorder(new LineBorder(HTONMapperTheme.ColorSoftGreen, 1));
                break;
            case Danger:
                setBackground(HTONMapperTheme.ColorBackgroundHover);
                setBorder(new LineBorder(HTONMapperTheme.ColorSoftRed, 1));
                break;
            default:
                setBackground(HTONMapperTheme.ColorBackgroundHover);
                setBorder(new LineBorder(HTONMapperTheme.ColorSoftBlue, 1));
                break;
        }
    }

    private void AttachHoverBehavior() {
        addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent EventArg) {
                    if (isEnabled()) {
                        ApplyHoverColors();
                    }
                }

                @Override
                public void mouseExited(MouseEvent EventArg) {
                    if (isEnabled()) {
                        ApplyRestingColors();
                    }
                }
            }
        );
    }

    @Override
    public void setEnabled(boolean IsEnabledArg) {
        super.setEnabled(IsEnabledArg);
        if (!IsEnabledArg) {
            setBackground(HTONMapperTheme.ColorBackgroundInset);
            setForeground(HTONMapperTheme.ColorTextMuted);
            setBorder(new LineBorder(HTONMapperTheme.ColorBorderMuted, 1));
        } else {
            ApplyRestingColors();
        }
    }
}
