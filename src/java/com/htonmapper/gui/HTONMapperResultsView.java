package com.htonmapper.gui;

import com.htonmapper.core.PortResult;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public class HTONMapperResultsView extends HTONMapperPanel {

    private DefaultTableModel TableModel;
    private HTONMapperTable ResultsTable;
    private HTONMapperLabel CountLabel;

    public HTONMapperResultsView() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 14, 0));

        HTONMapperLabel TitleLabel = new HTONMapperLabel("Scan Results");
        TitleLabel.SetHeadingStyle();

        CountLabel = new HTONMapperLabel("0 open ports found");
        CountLabel.SetSmallStyle();

        HeadingPanel.add(TitleLabel, BorderLayout.WEST);
        HeadingPanel.add(CountLabel, BorderLayout.EAST);

        String[] ColumnNames = {"Port", "Status", "Service", "Response Time (ms)"};
        TableModel = new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int RowArg, int ColumnArg) {
                return false;
            }
        };

        ResultsTable = new HTONMapperTable(TableModel);

        JScrollPane ScrollContainer = new JScrollPane(ResultsTable);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));
        ScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundInset);

        add(HeadingPanel, BorderLayout.NORTH);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    public void AddResultRow(PortResult ResultArg) {
        SwingUtilities.invokeLater(() -> {
            TableModel.addRow(new Object[]{
                    ResultArg.GetPortNumber(),
                    ResultArg.GetPortStatus(),
                    ResultArg.GetServiceName(),
                    ResultArg.GetResponseTimeMs()
            });
            CountLabel.setText(TableModel.getRowCount() + " open ports found");
        });
    }

    public void ClearResults() {
        SwingUtilities.invokeLater(() -> {
            TableModel.setRowCount(0);
            CountLabel.setText("0 open ports found");
        });
    }
}
