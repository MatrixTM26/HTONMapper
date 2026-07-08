package com.htonmapper.gui;

import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public class HTONMapperStatCard extends HTONMapperPanel {

    private HTONMapperLabel ValueLabel;

    public HTONMapperStatCard(String TitleText, String InitialValue, Color AccentColorArg) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        SetCardStyle();
        setMaximumSize(new Dimension(1000, 84));
        setPreferredSize(new Dimension(160, 84));

        HTONMapperLabel TitleLabel = new HTONMapperLabel(TitleText.toUpperCase());
        TitleLabel.SetSmallStyle();
        TitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ValueLabel = new HTONMapperLabel(InitialValue, AccentColorArg);
        ValueLabel.setFont(HTONMapperTheme.FontMonoHeading);
        ValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ValueLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        add(TitleLabel);
        add(ValueLabel);
    }

    public void UpdateValue(String NewValue) {
        ValueLabel.setText(NewValue);
    }
}
