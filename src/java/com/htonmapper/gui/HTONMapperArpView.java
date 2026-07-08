package com.htonmapper.gui;

import com.htonmapper.core.ArpTableEngine;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

public class HTONMapperArpView extends HTONMapperPanel {

    private final ArpTableEngine EngineInstance = new ArpTableEngine();
    private HTONMapperButton RefreshButton;
    private HTONMapperLabel CountLabel;
    private DefaultTableModel TableModel;
    private HTONMapperTable ArpTable;

    public HTONMapperArpView(Consumer<String> OnLogMessage) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("Local ARP Table");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Read the local machine's ARP cache for forensic reference");
        SubtitleLabel.SetSmallStyle();
        HTONMapperPanel TextStack = new HTONMapperPanel();
        TextStack.setLayout(new javax.swing.BoxLayout(TextStack, javax.swing.BoxLayout.Y_AXIS));
        TitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        SubtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        TextStack.add(TitleLabel);
        TextStack.add(SubtitleLabel);
        HeadingPanel.add(TextStack, BorderLayout.WEST);

        HTONMapperPanel ControlRow = new HTONMapperPanel(new BorderLayout());
        ControlRow.setBorder(new EmptyBorder(0, 0, 12, 0));
        RefreshButton = new HTONMapperButton("Refresh ARP Table", HTONMapperButton.ButtonVariant.Primary);
        RefreshButton.addActionListener(EventArg -> HandleRefreshClicked(OnLogMessage));
        HTONMapperPanel ButtonWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ButtonWrap.add(RefreshButton);
        CountLabel = new HTONMapperLabel("0 entries");
        CountLabel.SetSmallStyle();
        ControlRow.add(ButtonWrap, BorderLayout.WEST);
        ControlRow.add(CountLabel, BorderLayout.EAST);

        String[] ColumnNames = {"IP Address", "MAC Address", "Interface", "Type / State"};
        TableModel = new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int RowArg, int ColumnArg) {
                return false;
            }
        };
        ArpTable = new HTONMapperTable(TableModel, -1);
        JScrollPane ScrollContainer = new JScrollPane(ArpTable);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 2));
        ScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundInset);

        HTONMapperPanel TopStack = new HTONMapperPanel(new BorderLayout());
        TopStack.add(ControlRow, BorderLayout.NORTH);

        add(HeadingPanel, BorderLayout.NORTH);
        add(TopStack, BorderLayout.PAGE_START);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    private void HandleRefreshClicked(Consumer<String> OnLogMessage) {
        RefreshButton.setEnabled(false);
        TableModel.setRowCount(0);

        EngineInstance.ReadLocalArpTable(
                EntryArg -> SwingUtilities.invokeLater(() -> {
                    TableModel.addRow(new Object[]{
                            EntryArg.GetIpAddress(), EntryArg.GetMacAddress(), EntryArg.GetInterfaceName(), EntryArg.GetEntryType()
                    });
                    CountLabel.setText(TableModel.getRowCount() + " entries");
                }),
                () -> SwingUtilities.invokeLater(() -> RefreshButton.setEnabled(true)),
                OnLogMessage
        );
    }
}
