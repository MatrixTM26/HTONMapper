package com.htonmapper.gui;

import com.htonmapper.core.SslScannerEngine;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class HTONMapperSslView extends HTONMapperPanel {

    private final SslScannerEngine EngineInstance = new SslScannerEngine();
    private HTONMapperTextField HostInputField;
    private HTONMapperTextField PortInputField;
    private HTONMapperButton ScanButton;
    private HTONMapperLabel StatusLabel;
    private HTONMapperLabel SubjectValue;
    private HTONMapperLabel IssuerValue;
    private HTONMapperLabel ValidFromValue;
    private HTONMapperLabel ValidUntilValue;
    private HTONMapperLabel DaysRemainingValue;
    private HTONMapperLabel ProtocolValue;
    private HTONMapperLabel CipherValue;
    private HTONMapperLabel SignatureAlgValue;
    private HTONMapperLabel SelfSignedValue;

    public HTONMapperSslView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("SSL / TLS Certificate Scanner");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Inspect certificate chain, expiry, protocol, and cipher suite");
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
        HostInputField = new HTONMapperTextField("example.com");
        PortInputField = new HTONMapperTextField("443");
        PortInputField.setPreferredSize(new java.awt.Dimension(70, 30));

        HTONMapperPanel InputRow = new HTONMapperPanel(new BorderLayout(10, 0));
        InputRow.setBackground(HTONMapperTheme.ColorBackgroundPanel);
        InputRow.add(HostInputField, BorderLayout.CENTER);
        InputRow.add(PortInputField, BorderLayout.EAST);
        FormCard.add(InputRow, BorderLayout.CENTER);

        ScanButton = new HTONMapperButton("Scan Certificate", HTONMapperButton.ButtonVariant.Primary);
        ScanButton.addActionListener(EventArg -> HandleScanClicked(OnLogMessage));
        HTONMapperPanel ButtonWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ButtonWrap.add(ScanButton);
        FormCard.add(ButtonWrap, BorderLayout.EAST);

        StatusLabel = new HTONMapperLabel("Idle");
        StatusLabel.SetSmallStyle();
        StatusLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        HTONMapperPanel ResultCard = new HTONMapperPanel();
        ResultCard.setLayout(new javax.swing.BoxLayout(ResultCard, javax.swing.BoxLayout.Y_AXIS));
        ResultCard.SetCardStyle();

        SubjectValue = BuildResultRow(ResultCard, "Subject");
        IssuerValue = BuildResultRow(ResultCard, "Issuer");
        ValidFromValue = BuildResultRow(ResultCard, "Valid From");
        ValidUntilValue = BuildResultRow(ResultCard, "Valid Until");
        DaysRemainingValue = BuildResultRow(ResultCard, "Days Until Expiry");
        ProtocolValue = BuildResultRow(ResultCard, "Protocol Version");
        CipherValue = BuildResultRow(ResultCard, "Cipher Suite");
        SignatureAlgValue = BuildResultRow(ResultCard, "Signature Algorithm");
        SelfSignedValue = BuildResultRow(ResultCard, "Self Signed");

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
        RowPanel.setMaximumSize(new java.awt.Dimension(1000, 28));
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
        int PortValue;
        try {
            PortValue = Integer.parseInt(PortInputField.getText().trim());
        } catch (NumberFormatException ExceptionArg) {
            PortValue = 443;
        }
        if (HostValue.isEmpty()) {
            return;
        }

        ScanButton.setEnabled(false);
        StatusLabel.setText("Performing TLS handshake...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);

        EngineInstance.ScanCertificate(
            HostValue,
            PortValue,
            5000,
            ResultArg ->
                SwingUtilities.invokeLater(() -> {
                    SubjectValue.setText(ResultArg.GetSubjectName());
                    IssuerValue.setText(ResultArg.GetIssuerName());
                    ValidFromValue.setText(ResultArg.GetValidFromDate());
                    ValidUntilValue.setText(ResultArg.GetValidUntilDate());
                    DaysRemainingValue.setText(String.valueOf(ResultArg.GetDaysUntilExpiry()));
                    DaysRemainingValue.setForeground(ResultArg.GetIsExpired() ? HTONMapperTheme.ColorSoftRed : ResultArg.GetDaysUntilExpiry() < 30 ? HTONMapperTheme.ColorSoftYellow : HTONMapperTheme.ColorSoftGreen);
                    ProtocolValue.setText(ResultArg.GetProtocolVersion());
                    CipherValue.setText(ResultArg.GetCipherSuiteName());
                    SignatureAlgValue.setText(ResultArg.GetSignatureAlgorithm());
                    SelfSignedValue.setText(ResultArg.GetIsSelfSigned() ? "Yes" : "No");
                    SelfSignedValue.setForeground(ResultArg.GetIsSelfSigned() ? HTONMapperTheme.ColorSoftYellow : HTONMapperTheme.ColorSoftGreen);
                    StatusLabel.setText(ResultArg.GetIsExpired() ? "Certificate is expired" : "Certificate is valid");
                    StatusLabel.setForeground(ResultArg.GetIsExpired() ? HTONMapperTheme.ColorSoftRed : HTONMapperTheme.ColorSoftGreen);
                    ScanButton.setEnabled(true);
                }),
            FailureMessage ->
                SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Scan failed: " + FailureMessage);
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                    ScanButton.setEnabled(true);
                }),
            OnLogMessage
        );
    }
}
