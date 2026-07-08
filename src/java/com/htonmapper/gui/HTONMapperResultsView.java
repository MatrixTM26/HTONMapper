package com.htonmapper.gui;

import com.htonmapper.core.PortResult;
import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class HTONMapperResultsView extends HTONMapperPanel {

    private DefaultTableModel TableModel;
    private HTONMapperTable ResultsTable;
    private HTONMapperLabel CountLabel;
    private final AtomicInteger OpenCount = new AtomicInteger(0);
    private final AtomicInteger FilteredCount = new AtomicInteger(0);
    private final AtomicInteger ClosedCount = new AtomicInteger(0);

    public HTONMapperResultsView() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 14, 0));

        HTONMapperLabel TitleLabel = new HTONMapperLabel("Scan Results");
        TitleLabel.SetHeadingStyle();

        CountLabel = new HTONMapperLabel("0 open  /  0 filtered  /  0 closed");
        CountLabel.SetSmallStyle();

        HeadingPanel.add(TitleLabel, BorderLayout.WEST);
        HeadingPanel.add(CountLabel, BorderLayout.EAST);

        String[] ColumnNames = { "Port", "Status", "Service", "Response Time (ms)" };
        TableModel = new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int RowArg, int ColumnArg) {
                return false;
            }
        };

        ResultsTable = new HTONMapperTable(TableModel, 1);

        JScrollPane ScrollContainer = new JScrollPane(ResultsTable);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 2));
        ScrollContainer.getViewport().setBackground(HTONMapperTheme.ColorBackgroundInset);

        add(HeadingPanel, BorderLayout.NORTH);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    public void AddResultRow(PortResult ResultArg) {
        SwingUtilities.invokeLater(() -> {
            TableModel.addRow(new Object[] { ResultArg.GetPortNumber(), ResultArg.GetPortStatus(), ResultArg.GetServiceName(), ResultArg.GetResponseTimeMs() });
            UpdateStatusCounter(ResultArg.GetPortStatus());
            RefreshCountLabel();
        });
    }

    private void UpdateStatusCounter(String StatusValue) {
        if (PortResult.StatusOpen.equals(StatusValue)) {
            OpenCount.incrementAndGet();
        } else if (PortResult.StatusFiltered.equals(StatusValue)) {
            FilteredCount.incrementAndGet();
        } else if (PortResult.StatusClosed.equals(StatusValue)) {
            ClosedCount.incrementAndGet();
        }
    }

    private void RefreshCountLabel() {
        CountLabel.setText(OpenCount.get() + " open  /  " + FilteredCount.get() + " filtered  /  " + ClosedCount.get() + " closed");
    }

    public int GetOpenCount() {
        return OpenCount.get();
    }

    public int GetFilteredCount() {
        return FilteredCount.get();
    }

    public int GetClosedCount() {
        return ClosedCount.get();
    }

    public void ClearResults() {
        SwingUtilities.invokeLater(() -> {
            TableModel.setRowCount(0);
            OpenCount.set(0);
            FilteredCount.set(0);
            ClosedCount.set(0);
            RefreshCountLabel();
        });
    }
}
