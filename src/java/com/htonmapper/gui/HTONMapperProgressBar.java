package com.htonmapper.gui;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class HTONMapperProgressBar extends JProgressBar {

    public HTONMapperProgressBar() {
        super(0, 100);
        ApplyBaseStyle();
    }

    private void ApplyBaseStyle() {
        setStringPainted(true);
        setFont(HTONMapperTheme.FontMonoSmallBold);
        setForeground(HTONMapperTheme.ColorSoftGreen);
        setBackground(HTONMapperTheme.ColorBackgroundInset);
        setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
        setValue(0);
        setString("0%");
        setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics GraphicsArg, javax.swing.JComponent ComponentArg) {
                Graphics2D Graphics2DArg = (Graphics2D) GraphicsArg.create();
                Graphics2DArg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int BarWidth = getWidth();
                int BarHeight = getHeight();
                int FillWidth = (int) (BarWidth * (getPercentComplete()));

                Graphics2DArg.setColor(HTONMapperTheme.ColorBackgroundInset);
                Graphics2DArg.fillRect(0, 0, BarWidth, BarHeight);

                Graphics2DArg.setColor(HTONMapperTheme.ColorSoftGreenDim);
                Graphics2DArg.fillRect(0, 0, FillWidth, BarHeight);

                Graphics2DArg.setColor(HTONMapperTheme.ColorSoftGreen);
                Graphics2DArg.fillRect(0, 0, FillWidth, 2);

                String PercentText = getString();
                Graphics2DArg.setFont(HTONMapperTheme.FontMonoSmallBold);
                int TextWidth = Graphics2DArg.getFontMetrics().stringWidth(PercentText);
                int TextX = (BarWidth - TextWidth) / 2;
                int TextY = (BarHeight + Graphics2DArg.getFontMetrics().getAscent()) / 2 - 2;
                Graphics2DArg.setColor(HTONMapperTheme.ColorTextPrimary);
                Graphics2DArg.drawString(PercentText, TextX, TextY);

                Graphics2DArg.dispose();
            }
        });
    }

    public void UpdatePercentValue(int CurrentAmount, int TotalAmount) {
        int PercentValue = TotalAmount == 0 ? 0 : (int) (((double) CurrentAmount / TotalAmount) * 100);
        SwingUtilities.invokeLater(() -> {
            setValue(PercentValue);
            setString(PercentValue + "%  (" + CurrentAmount + "/" + TotalAmount + ")");
        });
    }

    public void ResetProgress() {
        SwingUtilities.invokeLater(() -> {
            setValue(0);
            setString("0%");
        });
    }

    public void MarkComplete() {
        SwingUtilities.invokeLater(() -> {
            setValue(100);
            setString("100%  Complete");
        });
    }
}
