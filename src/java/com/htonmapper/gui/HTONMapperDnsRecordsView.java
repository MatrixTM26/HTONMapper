package com.htonmapper.gui;

import com.htonmapper.core.DnsLookupEngine;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class HTONMapperDnsRecordsView extends HTONMapperPanel {

    private final DnsLookupEngine EngineInstance = new DnsLookupEngine();
    private HTONMapperTextField HostInputField;
    private HTONMapperButton LookupButton;
    private DefaultTableModel TableModel;
    private HTONMapperTable RecordsTable;
    private HTONMapperLabel StatusLabel;

    public HTONMapperDnsRecordsView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("DNS Records");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Query A, AAAA, MX, TXT, NS, CNAME, and SOA records");
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
        LookupButton = new HTONMapperButton("Lookup Records", HTONMapperButton.ButtonVariant.Primary);
        LookupButton.addActionListener(EventArg -> HandleLookupClicked(OnLogMessage));
        FormCard.add(HostInputField, BorderLayout.CENTER);
        HTONMapperPanel ButtonWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ButtonWrap.add(LookupButton);
        FormCard.add(ButtonWrap, BorderLayout.EAST);

        StatusLabel = new HTONMapperLabel("Idle");
        StatusLabel.SetSmallStyle();
        StatusLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        String[] ColumnNames = { "Record Type", "Value", "Query Time (ms)" };
        TableModel = new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int RowArg, int ColumnArg) {
                return false;
            }
        };
        RecordsTable = new HTONMapperTable(TableModel, -1);
        JScrollPane ScrollContainer = new JScrollPane(RecordsTable);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 2));
        ScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundInset);

        HTONMapperPanel TopStack = new HTONMapperPanel(new BorderLayout());
        TopStack.add(FormCard, BorderLayout.NORTH);
        TopStack.add(StatusLabel, BorderLayout.SOUTH);

        add(HeadingPanel, BorderLayout.NORTH);
        add(TopStack, BorderLayout.PAGE_START);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    private void HandleLookupClicked(Consumer<String> OnLogMessage) {
        String HostValue = HostInputField.getText().trim();
        if (HostValue.isEmpty()) {
            return;
        }
        TableModel.setRowCount(0);
        LookupButton.setEnabled(false);
        StatusLabel.setText("Querying DNS records...");
        StatusLabel.setForeground(HTONMapperTheme.ColorTextMuted);

        EngineInstance.ResolveAllRecords(
            HostValue,
            RecordArg -> SwingUtilities.invokeLater(() -> TableModel.addRow(new Object[] { RecordArg.GetRecordType(), RecordArg.GetRecordValue(), RecordArg.GetQueryTimeMs() })),
            () ->
                SwingUtilities.invokeLater(() -> {
                    LookupButton.setEnabled(true);
                    int FoundCount = TableModel.getRowCount();
                    StatusLabel.setText(FoundCount + " record(s) found");
                    StatusLabel.setForeground(FoundCount > 0 ? HTONMapperTheme.ColorSoftGreen : HTONMapperTheme.ColorSoftYellow);
                }),
            OnLogMessage
        );
    }
}
