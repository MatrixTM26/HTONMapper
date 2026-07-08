package com.htonmapper.gui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HTONMapperTable extends JTable {

    public HTONMapperTable(DefaultTableModel TableModelArg) {
        super(TableModelArg);
        ApplyBaseStyle();
        ApplyRowRenderer();
    }

    public HTONMapperTable(DefaultTableModel TableModelArg, int StatusColumnIndex) {
        super(TableModelArg);
        ApplyBaseStyle();
        ApplyRowRenderer(StatusColumnIndex);
    }

    private void ApplyBaseStyle() {
        setFont(HTONMapperTheme.FontMono);
        setBackground(HTONMapperTheme.ColorBackgroundInset);
        setForeground(HTONMapperTheme.ColorTextPrimary);
        setSelectionBackground(HTONMapperTheme.ColorSelection);
        setSelectionForeground(HTONMapperTheme.ColorTextPrimary);
        setRowHeight(26);
        setGridColor(HTONMapperTheme.ColorBorderMuted);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setIntercellSpacing(new java.awt.Dimension(0, 1));

        getTableHeader().setFont(HTONMapperTheme.FontMonoBold);
        getTableHeader().setBackground(HTONMapperTheme.ColorBackgroundHeader);
        getTableHeader().setForeground(HTONMapperTheme.ColorSoftBlue);
        getTableHeader().setPreferredSize(new java.awt.Dimension(0, 32));
    }

    private void ApplyRowRenderer() {
        ApplyRowRenderer(1);
    }

    private void ApplyRowRenderer(int StatusColumnIndex) {
        DefaultTableCellRenderer RowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable TableArg, Object ValueArg, boolean IsSelected, boolean HasFocus, int RowIndex, int ColumnIndex) {
                Component CellComponent = super.getTableCellRendererComponent(TableArg, ValueArg, IsSelected, HasFocus, RowIndex, ColumnIndex);
                if (!IsSelected) {
                    CellComponent.setBackground(RowIndex % 2 == 0 ? HTONMapperTheme.ColorBackgroundInset : HTONMapperTheme.ColorRowAlt);
                    CellComponent.setForeground(HTONMapperTheme.ColorTextPrimary);
                }
                if (ColumnIndex == StatusColumnIndex) {
                    ApplyStatusColor(CellComponent, String.valueOf(ValueArg));
                }
                setBorder(new javax.swing.border.EmptyBorder(0, 10, 0, 10));
                return CellComponent;
            }
        };
        for (int ColumnIndex = 0; ColumnIndex < getColumnCount(); ColumnIndex++) {
            getColumnModel().getColumn(ColumnIndex).setCellRenderer(RowRenderer);
        }
    }

    private void ApplyStatusColor(Component CellComponent, String ValueArg) {
        if ("OPEN".equals(ValueArg)) {
            CellComponent.setForeground(HTONMapperTheme.ColorSoftGreen);
        } else if ("FILTERED".equals(ValueArg)) {
            CellComponent.setForeground(HTONMapperTheme.ColorSoftYellow);
        } else if ("CLOSED".equals(ValueArg)) {
            CellComponent.setForeground(HTONMapperTheme.ColorTextMuted);
        }
    }
}
