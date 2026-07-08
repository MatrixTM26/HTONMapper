package com.htonmapper.gui;

import com.htonmapper.core.TechScannerEngine;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

public class HTONMapperTechStackView extends HTONMapperPanel {

    private final TechScannerEngine EngineInstance = new TechScannerEngine();
    private HTONMapperTextField UrlInputField;
    private HTONMapperButton ScanButton;
    private HTONMapperLabel StatusLabel;
    private HTONMapperLabel StatusCodeValue;
    private HTONMapperLabel ServerValue;
    private HTONMapperLabel PoweredByValue;
    private HTONMapperLabel TechnologiesValue;
    private HTONMapperLabel OutdatedValue;
    private HTONMapperLabel MissingHeadersValue;

    public HTONMapperTechStackView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("Tech Stack & Security Headers");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Detect server banners, technologies, and missing security headers");
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
        UrlInputField = new HTONMapperTextField("https://example.com");
        FormCard.add(UrlInputField, BorderLayout.CENTER);
        ScanButton = new HTONMapperButton("Scan Headers", HTONMapperButton.ButtonVariant.Primary);
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

        StatusCodeValue = BuildResultRow(ResultCard, "HTTP Status Code");
        ServerValue = BuildResultRow(ResultCard, "Server Banner");
        PoweredByValue = BuildResultRow(ResultCard, "X-Powered-By");
        TechnologiesValue = BuildResultRow(ResultCard, "Detected Technologies");
        OutdatedValue = BuildResultRow(ResultCard, "Outdated Version Signature");
        MissingHeadersValue = BuildResultRow(ResultCard, "Missing Security Headers");

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
        KeyLabel.setPreferredSize(new java.awt.Dimension(220, 20));

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
        String UrlValue = UrlInputField.getText().trim();
        if (UrlValue.isEmpty()) {
            return;
        }
        ScanButton.setEnabled(false);
        StatusLabel.setText("Sending request...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);

        EngineInstance.ScanHeaders(
                UrlValue,
                5000,
                ResultArg -> SwingUtilities.invokeLater(() -> {
                    StatusCodeValue.setText(String.valueOf(ResultArg.GetStatusCode()));
                    ServerValue.setText(ResultArg.GetServerBanner());
                    PoweredByValue.setText(ResultArg.GetPoweredByBanner());
                    TechnologiesValue.setText(String.join(", ", ResultArg.GetDetectedTechnologies()));
                    OutdatedValue.setText(ResultArg.GetHasOutdatedSignature() ? "Yes, outdated version detected" : "No known outdated signature");
                    OutdatedValue.setForeground(ResultArg.GetHasOutdatedSignature() ? HTONMapperTheme.ColorSoftRed : HTONMapperTheme.ColorSoftGreen);
                    int MissingCount = ResultArg.GetMissingSecurityHeaders().size();
                    MissingHeadersValue.setText(MissingCount == 0 ? "None missing" : String.join(", ", ResultArg.GetMissingSecurityHeaders()));
                    MissingHeadersValue.setForeground(MissingCount == 0 ? HTONMapperTheme.ColorSoftGreen : HTONMapperTheme.ColorSoftYellow);
                    StatusLabel.setText("Scan completed");
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftGreen);
                    ScanButton.setEnabled(true);
                }),
                FailureMessage -> SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Scan failed: " + FailureMessage);
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                    ScanButton.setEnabled(true);
                }),
                OnLogMessage
        );
    }
}
