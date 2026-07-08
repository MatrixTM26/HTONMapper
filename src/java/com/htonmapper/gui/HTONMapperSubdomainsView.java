package com.htonmapper.gui;

import com.htonmapper.core.SubdomainDiscoveryEngine;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

public class HTONMapperSubdomainsView extends HTONMapperPanel {

    private final SubdomainDiscoveryEngine EngineInstance = new SubdomainDiscoveryEngine();
    private HTONMapperTextField DomainInputField;
    private HTONMapperButton StartButton;
    private HTONMapperButton StopButton;
    private HTONMapperProgressBar ProgressIndicator;
    private DefaultTableModel TableModel;
    private HTONMapperTable SubdomainsTable;
    private HTONMapperLabel CountLabel;

    public HTONMapperSubdomainsView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("Subdomain Discovery");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Wordlist based subdomain brute force with DNS resolution");
        SubtitleLabel.SetSmallStyle();
        HTONMapperPanel TextStack = new HTONMapperPanel();
        TextStack.setLayout(new javax.swing.BoxLayout(TextStack, javax.swing.BoxLayout.Y_AXIS));
        TitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        SubtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        TextStack.add(TitleLabel);
        TextStack.add(SubtitleLabel);
        HeadingPanel.add(TextStack, BorderLayout.WEST);

        HTONMapperPanel FormCard = new HTONMapperPanel(new BorderLayout());
        FormCard.SetCardStyle();

        HTONMapperPanel InputRow = new HTONMapperPanel(new BorderLayout(10, 0));
        InputRow.setBackground(HTONMapperTheme.ColorBackgroundPanel);
        DomainInputField = new HTONMapperTextField("example.com");
        InputRow.add(DomainInputField, BorderLayout.CENTER);

        HTONMapperPanel ButtonRow = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        StartButton = new HTONMapperButton("Start Discovery", HTONMapperButton.ButtonVariant.Primary);
        StopButton = new HTONMapperButton("Stop", HTONMapperButton.ButtonVariant.Danger);
        StopButton.setEnabled(false);
        StartButton.addActionListener(EventArg -> HandleStartClicked(OnLogMessage));
        StopButton.addActionListener(EventArg -> HandleStopClicked());
        ButtonRow.add(StartButton);
        ButtonRow.add(StopButton);

        ProgressIndicator = new HTONMapperProgressBar();

        HTONMapperPanel FormStack = new HTONMapperPanel();
        FormStack.setLayout(new javax.swing.BoxLayout(FormStack, javax.swing.BoxLayout.Y_AXIS));
        InputRow.setAlignmentX(LEFT_ALIGNMENT);
        ButtonRow.setAlignmentX(LEFT_ALIGNMENT);
        ProgressIndicator.setAlignmentX(LEFT_ALIGNMENT);
        FormStack.add(InputRow);
        FormStack.add(BuildSpacer(10));
        FormStack.add(ButtonRow);
        FormStack.add(BuildSpacer(10));
        FormStack.add(ProgressIndicator);
        FormCard.add(FormStack, BorderLayout.CENTER);

        CountLabel = new HTONMapperLabel("0 subdomains found");
        CountLabel.SetSmallStyle();
        CountLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        String[] ColumnNames = {"Subdomain", "Resolved IP", "Response Time (ms)"};
        TableModel = new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int RowArg, int ColumnArg) {
                return false;
            }
        };
        SubdomainsTable = new HTONMapperTable(TableModel, -1);
        JScrollPane ScrollContainer = new JScrollPane(SubdomainsTable);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 2));
        ScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundInset);

        HTONMapperPanel TopStack = new HTONMapperPanel(new BorderLayout());
        TopStack.add(FormCard, BorderLayout.NORTH);
        TopStack.add(CountLabel, BorderLayout.SOUTH);

        add(HeadingPanel, BorderLayout.NORTH);
        add(TopStack, BorderLayout.PAGE_START);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    private HTONMapperPanel BuildSpacer(int HeightArg) {
        HTONMapperPanel SpacerPanel = new HTONMapperPanel();
        SpacerPanel.setMaximumSize(new java.awt.Dimension(1000, HeightArg));
        SpacerPanel.setPreferredSize(new java.awt.Dimension(1, HeightArg));
        return SpacerPanel;
    }

    private void HandleStartClicked(Consumer<String> OnLogMessage) {
        String DomainValue = DomainInputField.getText().trim();
        if (DomainValue.isEmpty()) {
            return;
        }
        TableModel.setRowCount(0);
        StartButton.setEnabled(false);
        StopButton.setEnabled(true);
        ProgressIndicator.ResetProgress();

        EngineInstance.StartDiscovery(
                DomainValue,
                60,
                ResultArg -> SwingUtilities.invokeLater(() -> {
                    TableModel.addRow(new Object[]{
                            ResultArg.GetSubdomainName(), ResultArg.GetResolvedIpAddress(), ResultArg.GetResponseTimeMs()
                    });
                    CountLabel.setText(TableModel.getRowCount() + " subdomains found");
                }),
                ProgressIndicator::UpdatePercentValue,
                () -> SwingUtilities.invokeLater(() -> {
                    StartButton.setEnabled(true);
                    StopButton.setEnabled(false);
                    ProgressIndicator.MarkComplete();
                }),
                OnLogMessage
        );
    }

    private void HandleStopClicked() {
        EngineInstance.StopDiscovery();
        StartButton.setEnabled(true);
        StopButton.setEnabled(false);
    }
}
