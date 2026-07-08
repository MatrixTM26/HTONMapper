package com.htonmapper.gui;

import com.htonmapper.core.NetworkOriginEngine;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class HTONMapperOriginView extends HTONMapperPanel {

    private final NetworkOriginEngine EngineInstance = new NetworkOriginEngine();
    private HTONMapperTextField HostInputField;
    private HTONMapperButton ScanButton;
    private HTONMapperLabel StatusLabel;
    private HTONMapperLabel TorValue;
    private HTONMapperLabel HostingValue;
    private HTONMapperLabel OrganizationValue;
    private HTONMapperLabel CountryValue;
    private HTONMapperLabel SummaryValue;

    public HTONMapperOriginView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("Network Origin Check");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Check whether a target is a Tor exit node, VPN, or hosting provider");
        SubtitleLabel.SetSmallStyle();
        HTONMapperPanel TextStack = new HTONMapperPanel();
        TextStack.setLayout(new javax.swing.BoxLayout(TextStack, javax.swing.BoxLayout.Y_AXIS));
        TitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        SubtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        TextStack.add(TitleLabel);
        TextStack.add(SubtitleLabel);
        HeadingPanel.add(TextStack, BorderLayout.WEST);

        HTONMapperPanel FormCard = new HTONMapperPanel(new BorderLayout(10, 0));
        FormCard.SetCardStyle();
        HostInputField = new HTONMapperTextField("8.8.8.8");
        FormCard.add(HostInputField, BorderLayout.CENTER);
        ScanButton = new HTONMapperButton("Analyze Origin", HTONMapperButton.ButtonVariant.Primary);
        ScanButton.addActionListener(EventArg -> HandleScanClicked(OnLogMessage));
        HTONMapperPanel ButtonWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ButtonWrap.add(ScanButton);
        FormCard.add(ButtonWrap, BorderLayout.EAST);

        StatusLabel = new HTONMapperLabel("Idle");
        StatusLabel.SetSmallStyle();
        StatusLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        HTONMapperPanel ResultCard = new HTONMapperPanel();
        ResultCard.setLayout(new javax.swing.BoxLayout(ResultCard, javax.swing.BoxLayout.Y_AXIS));
        ResultCard.SetCardStyle();

        TorValue = BuildResultRow(ResultCard, "Tor Exit Node");
        HostingValue = BuildResultRow(ResultCard, "Hosting / VPN Range");
        OrganizationValue = BuildResultRow(ResultCard, "Organization");
        CountryValue = BuildResultRow(ResultCard, "Country");
        SummaryValue = BuildResultRow(ResultCard, "Summary");

        HTONMapperPanel StackPanel = new HTONMapperPanel();
        StackPanel.setLayout(new javax.swing.BoxLayout(StackPanel, javax.swing.BoxLayout.Y_AXIS));
        StackPanel.add(FormCard);
        StackPanel.add(StatusLabel);
        StackPanel.add(BuildSpacer(10));
        StackPanel.add(ResultCard);

        add(HeadingPanel, BorderLayout.NORTH);
        add(StackPanel, BorderLayout.CENTER);
    }

    private HTONMapperLabel BuildResultRow(HTONMapperPanel ParentCard, String KeyText) {
        HTONMapperPanel RowPanel = new HTONMapperPanel(new BorderLayout());
        RowPanel.setBorder(new EmptyBorder(6, 0, 6, 0));
        RowPanel.setAlignmentX(LEFT_ALIGNMENT);

        HTONMapperLabel KeyLabel = new HTONMapperLabel(KeyText, HTONMapperTheme.ColorSoftGreen);
        KeyLabel.SetBoldStyle();
        KeyLabel.setPreferredSize(new java.awt.Dimension(200, 20));

        HTONMapperLabel ValueLabel = new HTONMapperLabel("-", HTONMapperTheme.ColorTextPrimary);

        RowPanel.add(KeyLabel, BorderLayout.WEST);
        RowPanel.add(ValueLabel, BorderLayout.CENTER);
        ParentCard.add(RowPanel);
        return ValueLabel;
    }

    private HTONMapperPanel BuildSpacer(int HeightArg) {
        HTONMapperPanel SpacerPanel = new HTONMapperPanel();
        SpacerPanel.setMaximumSize(new java.awt.Dimension(1000, HeightArg));
        SpacerPanel.setPreferredSize(new java.awt.Dimension(1, HeightArg));
        return SpacerPanel;
    }

    private void HandleScanClicked(Consumer<String> OnLogMessage) {
        String HostValue = HostInputField.getText().trim();
        if (HostValue.isEmpty()) {
            return;
        }
        ScanButton.setEnabled(false);
        StatusLabel.setText("Analyzing origin...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);

        EngineInstance.AnalyzeOrigin(
            HostValue,
            6000,
            ResultArg ->
                SwingUtilities.invokeLater(() -> {
                    TorValue.setText(ResultArg.GetIsTorExitNode() ? "Yes" : "No");
                    TorValue.setForeground(ResultArg.GetIsTorExitNode() ? HTONMapperTheme.ColorSoftPurple : HTONMapperTheme.ColorSoftGreen);
                    HostingValue.setText(ResultArg.GetIsKnownHostingRange() ? "Yes" : "No");
                    HostingValue.setForeground(ResultArg.GetIsKnownHostingRange() ? HTONMapperTheme.ColorSoftYellow : HTONMapperTheme.ColorSoftGreen);
                    OrganizationValue.setText(ResultArg.GetOrganizationName());
                    CountryValue.setText(ResultArg.GetCountryCode());
                    SummaryValue.setText(ResultArg.GetDetectionSummary());
                    StatusLabel.setText("Analysis completed");
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftGreen);
                    ScanButton.setEnabled(true);
                }),
            FailureMessage ->
                SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Analysis failed: " + FailureMessage);
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                    ScanButton.setEnabled(true);
                }),
            OnLogMessage
        );
    }
}
