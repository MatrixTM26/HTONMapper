package com.htonmapper.gui;

import com.htonmapper.core.WafDetectionEngine;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class HTONMapperWafView extends HTONMapperPanel {

    private final WafDetectionEngine EngineInstance = new WafDetectionEngine();
    private HTONMapperTextField UrlInputField;
    private HTONMapperButton ScanButton;
    private HTONMapperLabel StatusLabel;
    private HTONMapperLabel DetectionValue;
    private HTONMapperLabel VendorValue;
    private HTONMapperLabel ReasonValue;

    public HTONMapperWafView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("WAF Detection");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Identify web application firewall vendors via response heuristics");
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
        ScanButton = new HTONMapperButton("Detect WAF", HTONMapperButton.ButtonVariant.Primary);
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

        DetectionValue = BuildResultRow(ResultCard, "WAF Detected");
        VendorValue = BuildResultRow(ResultCard, "Vendor");
        ReasonValue = BuildResultRow(ResultCard, "Detection Reason");

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
        String UrlValue = UrlInputField.getText().trim();
        if (UrlValue.isEmpty()) {
            return;
        }
        ScanButton.setEnabled(false);
        StatusLabel.setText("Probing target...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);

        EngineInstance.DetectWaf(
            UrlValue,
            5000,
            ResultArg ->
                SwingUtilities.invokeLater(() -> {
                    DetectionValue.setText(ResultArg.GetIsWafDetected() ? "Yes" : "No");
                    DetectionValue.setForeground(ResultArg.GetIsWafDetected() ? HTONMapperTheme.ColorSoftYellow : HTONMapperTheme.ColorSoftGreen);
                    VendorValue.setText(ResultArg.GetWafVendorName());
                    ReasonValue.setText(ResultArg.GetDetectionReason());
                    StatusLabel.setText("Probe completed");
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftGreen);
                    ScanButton.setEnabled(true);
                }),
            FailureMessage ->
                SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Probe failed: " + FailureMessage);
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                    ScanButton.setEnabled(true);
                }),
            OnLogMessage
        );
    }
}
