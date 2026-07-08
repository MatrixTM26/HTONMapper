package com.htonmapper.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.InetAddress;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class HTONMapperIpHostnameView extends HTONMapperPanel {

    private HTONMapperTextField TargetInputField;
    private HTONMapperButton ResolveButton;
    private HTONMapperLabel ResultHostnameValue;
    private HTONMapperLabel ResultIpValue;
    private HTONMapperLabel ResultCanonicalValue;
    private HTONMapperLabel ResultReachableValue;
    private HTONMapperLabel StatusLabel;

    public HTONMapperIpHostnameView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("IP Address / Hostname");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Resolve a hostname to its IP address or reverse lookup an IP");
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
        TargetInputField = new HTONMapperTextField("example.com");
        ResolveButton = new HTONMapperButton("Resolve", HTONMapperButton.ButtonVariant.Primary);
        ResolveButton.addActionListener(EventArg -> HandleResolveClicked(OnLogMessage));
        FormCard.add(TargetInputField, BorderLayout.CENTER);

        HTONMapperPanel ButtonWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ButtonWrap.add(ResolveButton);
        FormCard.add(ButtonWrap, BorderLayout.EAST);

        HTONMapperPanel ResultCard = new HTONMapperPanel();
        ResultCard.setLayout(new javax.swing.BoxLayout(ResultCard, javax.swing.BoxLayout.Y_AXIS));
        ResultCard.SetCardStyle();

        ResultHostnameValue = BuildResultRow(ResultCard, "Input Target");
        ResultIpValue = BuildResultRow(ResultCard, "Resolved IP Address(es)");
        ResultCanonicalValue = BuildResultRow(ResultCard, "Canonical Hostname");
        ResultReachableValue = BuildResultRow(ResultCard, "Reachability");

        StatusLabel = new HTONMapperLabel("Idle");
        StatusLabel.SetSmallStyle();
        StatusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        HTONMapperPanel StackPanel = new HTONMapperPanel();
        StackPanel.setLayout(new javax.swing.BoxLayout(StackPanel, javax.swing.BoxLayout.Y_AXIS));
        StackPanel.add(FormCard);
        StackPanel.add(BuildSpacer(16));
        StackPanel.add(ResultCard);
        StackPanel.add(StatusLabel);

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

    private void HandleResolveClicked(Consumer<String> OnLogMessage) {
        String TargetValue = TargetInputField.getText().trim();
        if (TargetValue.isEmpty()) {
            return;
        }
        StatusLabel.setText("Resolving...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);
        ResolveButton.setEnabled(false);

        Thread ResolveThread = new Thread(() -> {
            OnLogMessage.accept("[*] Resolving target: " + TargetValue);
            try {
                InetAddress[] AddressList = InetAddress.getAllByName(TargetValue);
                StringBuilder IpBuilder = new StringBuilder();
                for (InetAddress AddressArg : AddressList) {
                    if (IpBuilder.length() > 0) {
                        IpBuilder.append(", ");
                    }
                    IpBuilder.append(AddressArg.getHostAddress());
                }
                String CanonicalName = AddressList[0].getCanonicalHostName();
                boolean IsReachable = AddressList[0].isReachable(1500);

                SwingUtilities.invokeLater(() -> {
                    ResultHostnameValue.setText(TargetValue);
                    ResultIpValue.setText(IpBuilder.toString());
                    ResultCanonicalValue.setText(CanonicalName);
                    ResultReachableValue.setText(IsReachable ? "Reachable" : "No ICMP response");
                    ResultReachableValue.setForeground(IsReachable ? HTONMapperTheme.ColorSoftGreen : HTONMapperTheme.ColorSoftYellow);
                    StatusLabel.setText("Resolution complete");
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftGreen);
                    ResolveButton.setEnabled(true);
                });
                OnLogMessage.accept("[*] Resolved " + AddressList.length + " address(es) for " + TargetValue);
            } catch (Exception ExceptionArg) {
                SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Resolution failed: " + ExceptionArg.getClass().getSimpleName());
                    StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                    ResolveButton.setEnabled(true);
                });
                OnLogMessage.accept("[!] Resolution failed for " + TargetValue);
            }
        });
        ResolveThread.setDaemon(true);
        ResolveThread.start();
    }
}
