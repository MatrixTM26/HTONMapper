package com.htonmapper.gui;

import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Component;

public class HTONMapperAboutView extends HTONMapperPanel {

    public HTONMapperAboutView() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(32, 32, 32, 32));

        HTONMapperLabel TitleLabel = new HTONMapperLabel("HTONMapper");
        TitleLabel.SetHeadingStyle();
        TitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        HTONMapperLabel VersionLabel = new HTONMapperLabel("v1.0.0  -  Cross Platform TCP Port Scanner");
        VersionLabel.SetSmallStyle();
        VersionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        VersionLabel.setBorder(new EmptyBorder(4, 0, 20, 0));

        HTONMapperPanel InfoCard = new HTONMapperPanel();
        InfoCard.setLayout(new BoxLayout(InfoCard, BoxLayout.Y_AXIS));
        InfoCard.SetCardStyle();
        InfoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        InfoCard.setMaximumSize(new java.awt.Dimension(560, 220));

        InfoCard.add(BuildInfoLine("Engine", "Java Socket TCP Connect Scan"));
        InfoCard.add(BuildInfoLine("Platform", "Windows / Linux"));
        InfoCard.add(BuildInfoLine("Author", "Teuku Maulana"));
        InfoCard.add(BuildInfoLine("Certification", "Certified Ethical Hacker (CEH)"));
        InfoCard.add(BuildInfoLine("Affiliation", "DISKOMINFO Aceh Selatan"));

        HTONMapperLabel NoticeLabel = new HTONMapperLabel("Use only on networks you own or are authorized to test.");
        NoticeLabel.SetSmallStyle();
        NoticeLabel.setForeground(HTONMapperTheme.ColorSoftYellow);
        NoticeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        NoticeLabel.setBorder(new EmptyBorder(18, 0, 0, 0));

        add(TitleLabel);
        add(VersionLabel);
        add(InfoCard);
        add(NoticeLabel);
    }

    private HTONMapperPanel BuildInfoLine(String KeyText, String ValueText) {
        HTONMapperPanel LineRow = new HTONMapperPanel(new java.awt.BorderLayout());
        LineRow.setBorder(new EmptyBorder(4, 0, 4, 0));
        LineRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        LineRow.setMaximumSize(new java.awt.Dimension(1000, 26));

        HTONMapperLabel KeyLabel = new HTONMapperLabel(KeyText, HTONMapperTheme.ColorSoftGreen);
        KeyLabel.SetBoldStyle();
        KeyLabel.setPreferredSize(new java.awt.Dimension(140, 20));

        HTONMapperLabel ValueLabel = new HTONMapperLabel(ValueText, HTONMapperTheme.ColorTextPrimary);

        LineRow.add(KeyLabel, java.awt.BorderLayout.WEST);
        LineRow.add(ValueLabel, java.awt.BorderLayout.CENTER);
        return LineRow;
    }
}
