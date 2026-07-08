package com.htonmapper.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class HTONMapperSidebar extends HTONMapperPanel {

    private boolean IsExpanded;
    private final int ExpandedWidth = 210;
    private final int CollapsedWidth = 56;
    private final List<HTONMapperButton> NavButtonList = new ArrayList<>();
    private final List<HTONMapperLabel> NavLabelList = new ArrayList<>();
    private HTONMapperButton ActiveButton;
    private HTONMapperPanel NavColumn;

    public HTONMapperSidebar(Consumer<String> OnNavigate) {
        super(new BorderLayout());
        this.IsExpanded = true;
        setBackground(HTONMapperTheme.ColorBackgroundHeader);
        setBorder(new MatteBorder(0, 0, 0, 2, HTONMapperTheme.ColorBorderDefault));
        setPreferredSize(new Dimension(ExpandedWidth, 0));

        HTONMapperPanel HeaderStack = new HTONMapperPanel();
        HeaderStack.setLayout(new BoxLayout(HeaderStack, BoxLayout.Y_AXIS));
        HeaderStack.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        HeaderStack.add(BuildBurgerRow());
        HeaderStack.add(BuildBrandRow());
        HeaderStack.add(BuildDividerLine());

        NavColumn = new HTONMapperPanel();
        NavColumn.setLayout(new BoxLayout(NavColumn, BoxLayout.Y_AXIS));
        NavColumn.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        NavColumn.setBorder(new EmptyBorder(8, 0, 8, 0));

        AddSectionLabel("RECON");
        AddNavButton("Scanner", OnNavigate);
        AddNavButton("Results", OnNavigate);
        AddNavButton("IP / Hostname", OnNavigate);
        AddNavButton("DNS Records", OnNavigate);
        AddNavButton("Subdomains", OnNavigate);

        AddSectionLabel("SECURITY");
        AddNavButton("SSL / TLS", OnNavigate);
        AddNavButton("Tech Stack", OnNavigate);
        AddNavButton("WAF Detect", OnNavigate);
        AddNavButton("Origin Check", OnNavigate);

        AddSectionLabel("SYSTEM");
        AddNavButton("ARP Table", OnNavigate);
        AddNavButton("Dashboard", OnNavigate);
        AddNavButton("Log", OnNavigate);
        AddNavButton("About", OnNavigate);

        JScrollPane NavScrollContainer = new JScrollPane(NavColumn);
        NavScrollContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        NavScrollContainer.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        NavScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundHeader);
        NavScrollContainer.getVerticalScrollBar().setUnitIncrement(14);
        NavScrollContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(HeaderStack, BorderLayout.NORTH);
        add(NavScrollContainer, BorderLayout.CENTER);

        SetActiveButton(NavButtonList.get(0));
    }

    private HTONMapperPanel BuildBrandRow() {
        HTONMapperPanel BrandRow = new HTONMapperPanel();
        BrandRow.setLayout(new BoxLayout(BrandRow, BoxLayout.X_AXIS));
        BrandRow.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        BrandRow.setBorder(new EmptyBorder(2, 14, 12, 14));
        BrandRow.setMaximumSize(new Dimension(1000, 34));
        BrandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        HTONMapperLabel BrandLabel = new HTONMapperLabel("HTONMapper", HTONMapperTheme.ColorSoftBlue);
        BrandLabel.setFont(HTONMapperTheme.FontMonoBold);
        BrandRow.add(BrandLabel);
        NavLabelList.add(BrandLabel);
        return BrandRow;
    }

    private HTONMapperPanel BuildBurgerRow() {
        HTONMapperPanel BurgerRow = new HTONMapperPanel(new BorderLayout());
        BurgerRow.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        BurgerRow.setBorder(new EmptyBorder(10, 10, 8, 10));
        BurgerRow.setMaximumSize(new Dimension(1000, 46));
        BurgerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        HTONMapperButton BurgerButton = new HTONMapperButton("=");
        BurgerButton.setPreferredSize(new Dimension(34, 30));
        BurgerButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        BurgerButton.addActionListener(EventArg -> ToggleSidebar());

        HTONMapperPanel LeftAnchor = new HTONMapperPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        LeftAnchor.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        LeftAnchor.add(BurgerButton);

        BurgerRow.add(LeftAnchor, BorderLayout.WEST);
        return BurgerRow;
    }

    private HTONMapperPanel BuildDividerLine() {
        HTONMapperPanel DividerPanel = new HTONMapperPanel();
        DividerPanel.setBackground(HTONMapperTheme.ColorBorderMuted);
        DividerPanel.setMaximumSize(new Dimension(1000, 2));
        DividerPanel.setPreferredSize(new Dimension(1000, 2));
        return DividerPanel;
    }

    private void AddSectionLabel(String LabelText) {
        HTONMapperLabel SectionLabel = new HTONMapperLabel(LabelText);
        SectionLabel.SetSmallStyle();
        SectionLabel.setForeground(HTONMapperTheme.ColorTextMuted);
        SectionLabel.setFont(HTONMapperTheme.FontMonoSmallBold);
        SectionLabel.setBorder(new EmptyBorder(12, 16, 6, 0));
        SectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        SectionLabel.setMaximumSize(new Dimension(1000, 24));
        NavLabelList.add(SectionLabel);
        NavColumn.add(SectionLabel);
    }

    private void AddNavButton(String LabelText, Consumer<String> OnNavigate) {
        HTONMapperButton ButtonArg = BuildNavButton(LabelText, OnNavigate);
        NavColumn.add(ButtonArg);
        NavColumn.add(BuildSpacer(2));
    }

    private HTONMapperPanel BuildSpacer(int HeightArg) {
        HTONMapperPanel SpacerPanel = new HTONMapperPanel();
        SpacerPanel.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        SpacerPanel.setMaximumSize(new Dimension(1000, HeightArg));
        SpacerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return SpacerPanel;
    }

    private HTONMapperButton BuildNavButton(String LabelText, Consumer<String> OnNavigate) {
        HTONMapperButton ButtonArg = new HTONMapperButton("  " + LabelText);
        ButtonArg.setHorizontalAlignment(HTONMapperButton.LEFT);
        ButtonArg.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonArg.setMaximumSize(new Dimension(1000, 36));
        ButtonArg.setPreferredSize(new Dimension(ExpandedWidth, 36));
        ButtonArg.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorBackgroundHeader), new EmptyBorder(0, 8, 0, 0)));
        ButtonArg.setMargin(new java.awt.Insets(0, 10, 0, 0));

        ButtonArg.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent EventArg) {
                    if (ButtonArg != ActiveButton) {
                        ButtonArg.setBackground(HTONMapperTheme.ColorBackgroundHover);
                    }
                }

                @Override
                public void mouseExited(MouseEvent EventArg) {
                    if (ButtonArg != ActiveButton) {
                        ButtonArg.setBackground(HTONMapperTheme.ColorBackgroundHeader);
                    }
                }
            }
        );

        ButtonArg.addActionListener(EventArg -> {
            SetActiveButton(ButtonArg);
            OnNavigate.accept(LabelText);
        });

        NavButtonList.add(ButtonArg);
        return ButtonArg;
    }

    private void SetActiveButton(HTONMapperButton TargetButton) {
        for (HTONMapperButton ButtonArg : NavButtonList) {
            ButtonArg.setBackground(HTONMapperTheme.ColorBackgroundHeader);
            ButtonArg.setForeground(HTONMapperTheme.ColorTextSecondary);
            ButtonArg.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorBackgroundHeader), new EmptyBorder(0, 8, 0, 0)));
        }
        TargetButton.setBackground(HTONMapperTheme.ColorBackgroundHover);
        TargetButton.setForeground(HTONMapperTheme.ColorTextPrimary);
        TargetButton.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorSoftBlue), new EmptyBorder(0, 8, 0, 0)));
        ActiveButton = TargetButton;
    }

    private void ToggleSidebar() {
        IsExpanded = !IsExpanded;
        int TargetWidth = IsExpanded ? ExpandedWidth : CollapsedWidth;
        setPreferredSize(new Dimension(TargetWidth, 0));
        for (HTONMapperButton ButtonArg : NavButtonList) {
            ButtonArg.setVisible(IsExpanded);
        }
        for (HTONMapperLabel LabelArg : NavLabelList) {
            LabelArg.setVisible(IsExpanded);
        }
        revalidate();
        repaint();
    }
}
