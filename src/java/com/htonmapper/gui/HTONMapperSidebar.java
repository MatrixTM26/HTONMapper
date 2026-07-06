package com.htonmapper.gui;

import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HTONMapperSidebar extends HTONMapperPanel {

    private boolean IsExpanded;
    private final int ExpandedWidth = 190;
    private final int CollapsedWidth = 54;
    private final List<HTONMapperButton> NavButtonList = new ArrayList<>();
    private HTONMapperButton ActiveButton;

    public HTONMapperSidebar(Consumer<String> OnNavigate) {
        super();
        this.IsExpanded = true;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(HTONMapperTheme.ColorBackgroundHeader);
        setBorder(new MatteBorder(0, 0, 0, 1, HTONMapperTheme.ColorBorderDefault));
        setPreferredSize(new Dimension(ExpandedWidth, 0));

        add(BuildBrandRow());
        add(BuildBurgerRow());
        add(BuildDividerLine());
        add(BuildSpacer(10));

        HTONMapperButton ScannerNav = BuildNavButton("Scanner", OnNavigate);
        HTONMapperButton ResultsNav = BuildNavButton("Results", OnNavigate);
        HTONMapperButton LogNav = BuildNavButton("Log", OnNavigate);
        HTONMapperButton AboutNav = BuildNavButton("About", OnNavigate);

        add(ScannerNav);
        add(BuildSpacer(4));
        add(ResultsNav);
        add(BuildSpacer(4));
        add(LogNav);
        add(BuildSpacer(4));
        add(AboutNav);

        SetActiveButton(ScannerNav);
    }

    private HTONMapperPanel BuildBrandRow() {
        HTONMapperPanel BrandRow = new HTONMapperPanel();
        BrandRow.setLayout(new BoxLayout(BrandRow, BoxLayout.X_AXIS));
        BrandRow.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        BrandRow.setBorder(new EmptyBorder(14, 14, 10, 14));
        BrandRow.setMaximumSize(new Dimension(1000, 40));
        BrandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        HTONMapperLabel BrandLabel = new HTONMapperLabel("HTONMapper", HTONMapperTheme.ColorSoftBlue);
        BrandLabel.setFont(HTONMapperTheme.FontMonoBold);
        BrandRow.add(BrandLabel);
        return BrandRow;
    }

    private HTONMapperPanel BuildBurgerRow() {
        HTONMapperPanel BurgerRow = new HTONMapperPanel();
        BurgerRow.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        BurgerRow.setBorder(new EmptyBorder(0, 10, 10, 10));
        BurgerRow.setMaximumSize(new Dimension(1000, 44));
        BurgerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        HTONMapperButton BurgerButton = new HTONMapperButton("=");
        BurgerButton.setMaximumSize(new Dimension(34, 30));
        BurgerButton.setPreferredSize(new Dimension(34, 30));
        BurgerButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        BurgerButton.addActionListener(EventArg -> ToggleSidebar());
        BurgerRow.add(BurgerButton);
        return BurgerRow;
    }

    private HTONMapperPanel BuildDividerLine() {
        HTONMapperPanel DividerPanel = new HTONMapperPanel();
        DividerPanel.setBackground(HTONMapperTheme.ColorBorderMuted);
        DividerPanel.setMaximumSize(new Dimension(1000, 1));
        DividerPanel.setPreferredSize(new Dimension(1000, 1));
        return DividerPanel;
    }

    private HTONMapperPanel BuildSpacer(int HeightArg) {
        HTONMapperPanel SpacerPanel = new HTONMapperPanel();
        SpacerPanel.setBackground(HTONMapperTheme.ColorBackgroundHeader);
        SpacerPanel.setMaximumSize(new Dimension(1000, HeightArg));
        return SpacerPanel;
    }

    private HTONMapperButton BuildNavButton(String LabelText, Consumer<String> OnNavigate) {
        HTONMapperButton ButtonArg = new HTONMapperButton("  " + LabelText);
        ButtonArg.setHorizontalAlignment(HTONMapperButton.LEFT);
        ButtonArg.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonArg.setMaximumSize(new Dimension(1000, 38));
        ButtonArg.setBorder(new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorBackgroundHeader),
                new EmptyBorder(0, 8, 0, 0)
        ));
        ButtonArg.setMargin(new java.awt.Insets(0, 10, 0, 0));

        ButtonArg.addMouseListener(new MouseAdapter() {
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
        });

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
            ButtonArg.setBorder(new CompoundBorder(
                    new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorBackgroundHeader),
                    new EmptyBorder(0, 8, 0, 0)
            ));
        }
        TargetButton.setBackground(HTONMapperTheme.ColorBackgroundHover);
        TargetButton.setForeground(HTONMapperTheme.ColorTextPrimary);
        TargetButton.setBorder(new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, HTONMapperTheme.ColorSoftBlue),
                new EmptyBorder(0, 8, 0, 0)
        ));
        ActiveButton = TargetButton;
    }

    private void ToggleSidebar() {
        IsExpanded = !IsExpanded;
        int TargetWidth = IsExpanded ? ExpandedWidth : CollapsedWidth;
        setPreferredSize(new Dimension(TargetWidth, 0));
        for (HTONMapperButton ButtonArg : NavButtonList) {
            ButtonArg.setVisible(IsExpanded);
        }
        getComponent(0).setVisible(IsExpanded);
        revalidate();
        repaint();
    }
}
