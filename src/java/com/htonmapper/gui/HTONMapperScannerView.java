package com.htonmapper.gui;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.BiConsumer;

public class HTONMapperScannerView extends HTONMapperPanel {

    private HTONMapperTextField HostInputField;
    private HTONMapperTextField StartPortField;
    private HTONMapperTextField EndPortField;
    private HTONMapperTextField TimeoutField;
    private HTONMapperTextField ThreadCountField;
    private JCheckBox IncludeClosedCheckbox;
    private HTONMapperButton StartButton;
    private HTONMapperButton StopButton;
    private HTONMapperProgressBar ProgressIndicator;
    private HTONMapperLabel StatusLabel;

    public interface ScanStartHandler {
        void OnStart(String HostValue, int[] ParamsArg, boolean IncludeClosedPorts);
    }

    public HTONMapperScannerView(ScanStartHandler OnStartScan, Runnable OnStopScan) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = BuildHeadingPanel();
        HTONMapperPanel FormCard = BuildFormCard();
        HTONMapperPanel ControlCard = BuildControlCard(OnStartScan, OnStopScan);

        HTONMapperPanel StackPanel = new HTONMapperPanel();
        StackPanel.setLayout(new javax.swing.BoxLayout(StackPanel, javax.swing.BoxLayout.Y_AXIS));
        StackPanel.add(FormCard);
        StackPanel.add(BuildVerticalSpacer(18));
        StackPanel.add(ControlCard);

        add(HeadingPanel, BorderLayout.NORTH);
        add(StackPanel, BorderLayout.CENTER);
    }

    private HTONMapperPanel BuildHeadingPanel() {
        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));

        HTONMapperLabel TitleLabel = new HTONMapperLabel("Port Scanner");
        TitleLabel.SetHeadingStyle();

        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("TCP connect scan across a target host and port range");
        SubtitleLabel.SetSmallStyle();

        HTONMapperPanel TextStack = new HTONMapperPanel();
        TextStack.setLayout(new javax.swing.BoxLayout(TextStack, javax.swing.BoxLayout.Y_AXIS));
        TitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        SubtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        TextStack.add(TitleLabel);
        TextStack.add(SubtitleLabel);

        HeadingPanel.add(TextStack, BorderLayout.WEST);
        return HeadingPanel;
    }

    private HTONMapperPanel BuildFormCard() {
        HTONMapperPanel FormCard = new HTONMapperPanel(new GridBagLayout());
        FormCard.SetCardStyle();

        GridBagConstraints ConstraintArg = new GridBagConstraints();
        ConstraintArg.insets = new Insets(7, 10, 7, 10);
        ConstraintArg.fill = GridBagConstraints.HORIZONTAL;

        HostInputField = new HTONMapperTextField("127.0.0.1");
        StartPortField = new HTONMapperTextField("1");
        EndPortField = new HTONMapperTextField("1024");
        TimeoutField = new HTONMapperTextField("300");
        ThreadCountField = new HTONMapperTextField("150");

        AddFormRow(FormCard, ConstraintArg, 0, "Target Host", HostInputField);
        AddFormRow(FormCard, ConstraintArg, 1, "Start Port", StartPortField);
        AddFormRow(FormCard, ConstraintArg, 2, "End Port", EndPortField);
        AddFormRow(FormCard, ConstraintArg, 3, "Timeout (ms)", TimeoutField);
        AddFormRow(FormCard, ConstraintArg, 4, "Thread Count", ThreadCountField);

        IncludeClosedCheckbox = new JCheckBox("Include closed ports in results");
        IncludeClosedCheckbox.setFont(HTONMapperTheme.FontMono);
        IncludeClosedCheckbox.setForeground(HTONMapperTheme.ColorTextSecondary);
        IncludeClosedCheckbox.setBackground(HTONMapperTheme.ColorBackgroundPanel);
        IncludeClosedCheckbox.setFocusPainted(false);

        ConstraintArg.gridx = 0;
        ConstraintArg.gridy = 5;
        ConstraintArg.gridwidth = 2;
        ConstraintArg.insets = new Insets(4, 10, 4, 10);
        FormCard.add(IncludeClosedCheckbox, ConstraintArg);

        return FormCard;
    }

    private void AddFormRow(HTONMapperPanel FormCard, GridBagConstraints ConstraintArg, int RowIndex, String LabelText, HTONMapperTextField FieldArg) {
        ConstraintArg.gridx = 0;
        ConstraintArg.gridy = RowIndex;
        ConstraintArg.gridwidth = 1;
        ConstraintArg.weightx = 0.32;
        HTONMapperLabel LabelWidget = new HTONMapperLabel(LabelText, HTONMapperTheme.ColorSoftGreen);
        LabelWidget.SetBoldStyle();
        FormCard.add(LabelWidget, ConstraintArg);

        ConstraintArg.gridx = 1;
        ConstraintArg.weightx = 0.68;
        FormCard.add(FieldArg, ConstraintArg);
    }

    private HTONMapperPanel BuildControlCard(ScanStartHandler OnStartScan, Runnable OnStopScan) {
        HTONMapperPanel ControlCard = new HTONMapperPanel(new BorderLayout());
        ControlCard.SetCardStyle();

        HTONMapperPanel ButtonRow = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        StartButton = new HTONMapperButton("Start Scan", HTONMapperButton.ButtonVariant.Primary);
        StopButton = new HTONMapperButton("Stop Scan", HTONMapperButton.ButtonVariant.Danger);
        StopButton.setEnabled(false);

        StartButton.addActionListener(EventArg -> HandleStartClicked(OnStartScan));
        StopButton.addActionListener(EventArg -> HandleStopClicked(OnStopScan));

        ButtonRow.add(StartButton);
        ButtonRow.add(StopButton);

        StatusLabel = new HTONMapperLabel("Idle");
        StatusLabel.SetSmallStyle();
        StatusLabel.setBorder(new EmptyBorder(8, 2, 8, 0));

        ProgressIndicator = new HTONMapperProgressBar();

        HTONMapperPanel BottomStack = new HTONMapperPanel();
        BottomStack.setLayout(new javax.swing.BoxLayout(BottomStack, javax.swing.BoxLayout.Y_AXIS));
        BottomStack.add(StatusLabel);
        BottomStack.add(ProgressIndicator);

        ControlCard.add(ButtonRow, BorderLayout.NORTH);
        ControlCard.add(BottomStack, BorderLayout.SOUTH);
        return ControlCard;
    }

    private HTONMapperPanel BuildVerticalSpacer(int HeightArg) {
        HTONMapperPanel SpacerPanel = new HTONMapperPanel();
        SpacerPanel.setMaximumSize(new java.awt.Dimension(1000, HeightArg));
        SpacerPanel.setPreferredSize(new java.awt.Dimension(1, HeightArg));
        return SpacerPanel;
    }

    private void HandleStartClicked(ScanStartHandler OnStartScan) {
        try {
            String HostValue = HostInputField.getText().trim();
            int StartPortValue = Integer.parseInt(StartPortField.getText().trim());
            int EndPortValue = Integer.parseInt(EndPortField.getText().trim());
            int TimeoutValue = Integer.parseInt(TimeoutField.getText().trim());
            int ThreadValue = Integer.parseInt(ThreadCountField.getText().trim());

            if (HostValue.isEmpty() || StartPortValue < 1 || EndPortValue > 65535 || StartPortValue > EndPortValue) {
                StatusLabel.setText("Invalid input range");
                StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
                return;
            }

            SetScanningState(true);
            OnStartScan.OnStart(HostValue, new int[]{StartPortValue, EndPortValue, TimeoutValue, ThreadValue}, IncludeClosedCheckbox.isSelected());
        } catch (NumberFormatException ExceptionArg) {
            StatusLabel.setText("Invalid numeric input");
            StatusLabel.setForeground(HTONMapperTheme.ColorSoftRed);
        }
    }

    private void HandleStopClicked(Runnable OnStopScan) {
        OnStopScan.run();
        SetScanningState(false);
    }

    public void SetScanningState(boolean IsScanning) {
        SwingUtilities.invokeLater(() -> {
            StartButton.setEnabled(!IsScanning);
            StopButton.setEnabled(IsScanning);
            StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);
            StatusLabel.setText(IsScanning ? "Scanning target..." : "Idle");
            if (!IsScanning) {
                ProgressIndicator.MarkComplete();
            } else {
                ProgressIndicator.ResetProgress();
            }
        });
    }

    public String GetHostInputValue() {
        return HostInputField.getText().trim();
    }

    public HTONMapperProgressBar GetProgressIndicator() {
        return ProgressIndicator;
    }
}
