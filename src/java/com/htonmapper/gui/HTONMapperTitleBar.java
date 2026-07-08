package com.htonmapper.gui;

import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class HTONMapperTitleBar extends HTONMapperPanel {

    public HTONMapperTitleBar(String TitleText) {
        super(new BorderLayout());
        setBackground(HTONMapperTheme.ColorBackgroundHeader);
        setBorder(new MatteBorder(0, 0, 1, 0, HTONMapperTheme.ColorBorderDefault));
        setPreferredSize(new Dimension(0, 38));

        HTONMapperLabel TitleLabel = new HTONMapperLabel(TitleText);
        TitleLabel.setFont(HTONMapperTheme.FontMonoBold);
        TitleLabel.setForeground(HTONMapperTheme.ColorTextSecondary);
        TitleLabel.setHorizontalAlignment(HTONMapperLabel.CENTER);

        HTONMapperPanel ControlsPanel = new HTONMapperPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ControlsPanel.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        ControlsPanel.add(BuildDot(HTONMapperTheme.ColorTextMuted));
        ControlsPanel.add(BuildDot(HTONMapperTheme.ColorTextMuted));
        ControlsPanel.add(BuildDot(HTONMapperTheme.ColorSoftRed));
        ControlsPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 12));

        add(TitleLabel, BorderLayout.CENTER);
        add(ControlsPanel, BorderLayout.EAST);
    }

    private HTONMapperPanel BuildDot(java.awt.Color ColorArg) {
        HTONMapperPanel DotPanel = new HTONMapperPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics GraphicsArg) {
                java.awt.Graphics2D Graphics2DArg = (java.awt.Graphics2D) GraphicsArg.create();
                Graphics2DArg.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                Graphics2DArg.setColor(ColorArg);
                Graphics2DArg.fillOval(0, 0, 12, 12);
                Graphics2DArg.dispose();
            }
        };
        DotPanel.setOpaque(false);
        DotPanel.setPreferredSize(new Dimension(12, 12));
        return DotPanel;
    }
}
