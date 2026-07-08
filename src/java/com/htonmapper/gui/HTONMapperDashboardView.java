package com.htonmapper.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

public class HTONMapperDashboardView extends HTONMapperPanel {

    private HTONMapperGaugeChart PortGaugeChart;
    private HTONMapperStatCard OpenPortsCard;
    private HTONMapperStatCard FilteredPortsCard;
    private HTONMapperStatCard ClosedPortsCard;
    private HTONMapperStatCard SubdomainsCard;
    private HTONMapperLabel LegendOpenLabel;
    private HTONMapperLabel LegendFilteredLabel;
    private HTONMapperLabel LegendClosedLabel;

    public HTONMapperDashboardView() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperPanel HeadingPanel = new HTONMapperPanel(new BorderLayout());
        HeadingPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        HTONMapperLabel TitleLabel = new HTONMapperLabel("Dashboard");
        TitleLabel.SetHeadingStyle();
        HTONMapperLabel SubtitleLabel = new HTONMapperLabel("Summary of the latest scan results across all modules");
        SubtitleLabel.SetSmallStyle();
        HTONMapperPanel TextStack = new HTONMapperPanel();
        TextStack.setLayout(new BoxLayout(TextStack, BoxLayout.Y_AXIS));
        TitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        SubtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        TextStack.add(TitleLabel);
        TextStack.add(SubtitleLabel);
        HeadingPanel.add(TextStack, BorderLayout.WEST);

        HTONMapperPanel StatRow = new HTONMapperPanel(new GridLayout(1, 4, 14, 0));
        StatRow.setBorder(new EmptyBorder(0, 0, 18, 0));
        OpenPortsCard = new HTONMapperStatCard("Open Ports", "0", HTONMapperTheme.ColorSoftGreen);
        FilteredPortsCard = new HTONMapperStatCard("Filtered Ports", "0", HTONMapperTheme.ColorSoftYellow);
        ClosedPortsCard = new HTONMapperStatCard("Closed Ports", "0", HTONMapperTheme.ColorTextMuted);
        SubdomainsCard = new HTONMapperStatCard("Subdomains Found", "0", HTONMapperTheme.ColorSoftBlue);
        StatRow.add(OpenPortsCard);
        StatRow.add(FilteredPortsCard);
        StatRow.add(ClosedPortsCard);
        StatRow.add(SubdomainsCard);

        HTONMapperPanel ChartCard = new HTONMapperPanel(new BorderLayout());
        ChartCard.SetCardStyle();

        HTONMapperLabel ChartTitleLabel = new HTONMapperLabel("Port Status Distribution");
        ChartTitleLabel.SetBoldStyle();
        ChartTitleLabel.setForeground(HTONMapperTheme.ColorTextPrimary);
        ChartTitleLabel.setBorder(new EmptyBorder(0, 0, 14, 0));

        PortGaugeChart = new HTONMapperGaugeChart();
        HTONMapperPanel ChartWrap = new HTONMapperPanel(new FlowLayout(FlowLayout.CENTER));
        ChartWrap.add(PortGaugeChart);

        HTONMapperPanel LegendPanel = new HTONMapperPanel();
        LegendPanel.setLayout(new BoxLayout(LegendPanel, BoxLayout.Y_AXIS));
        LegendOpenLabel = BuildLegendRow(LegendPanel, "Open", HTONMapperTheme.ColorSoftGreen);
        LegendFilteredLabel = BuildLegendRow(LegendPanel, "Filtered", HTONMapperTheme.ColorSoftYellow);
        LegendClosedLabel = BuildLegendRow(LegendPanel, "Closed", HTONMapperTheme.ColorTextMuted);

        HTONMapperPanel ChartBody = new HTONMapperPanel(new BorderLayout());
        ChartBody.add(ChartWrap, BorderLayout.CENTER);
        ChartBody.add(LegendPanel, BorderLayout.EAST);

        ChartCard.add(ChartTitleLabel, BorderLayout.NORTH);
        ChartCard.add(ChartBody, BorderLayout.CENTER);

        HTONMapperPanel BodyStack = new HTONMapperPanel();
        BodyStack.setLayout(new BoxLayout(BodyStack, BoxLayout.Y_AXIS));
        BodyStack.add(StatRow);
        BodyStack.add(ChartCard);

        add(HeadingPanel, BorderLayout.NORTH);
        add(BodyStack, BorderLayout.CENTER);

        RefreshChart();
    }

    private HTONMapperLabel BuildLegendRow(HTONMapperPanel ParentPanel, String LabelText, Color ColorArg) {
        HTONMapperPanel RowPanel = new HTONMapperPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        RowPanel.setAlignmentX(LEFT_ALIGNMENT);
        HTONMapperPanel Swatch = new HTONMapperPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics GraphicsArg) {
                GraphicsArg.setColor(ColorArg);
                GraphicsArg.fillRect(0, 0, 12, 12);
            }
        };
        Swatch.setOpaque(false);
        Swatch.setPreferredSize(new Dimension(12, 12));
        HTONMapperLabel TextLabel = new HTONMapperLabel(LabelText + ": 0");
        TextLabel.SetSmallStyle();
        RowPanel.add(Swatch);
        RowPanel.add(TextLabel);
        ParentPanel.add(RowPanel);
        return TextLabel;
    }

    public void UpdateFromResults(int OpenCount, int FilteredCount, int ClosedCount, int SubdomainCount) {
        OpenPortsCard.UpdateValue(String.valueOf(OpenCount));
        FilteredPortsCard.UpdateValue(String.valueOf(FilteredCount));
        ClosedPortsCard.UpdateValue(String.valueOf(ClosedCount));
        SubdomainsCard.UpdateValue(String.valueOf(SubdomainCount));

        LegendOpenLabel.setText("Open: " + OpenCount);
        LegendFilteredLabel.setText("Filtered: " + FilteredCount);
        LegendClosedLabel.setText("Closed: " + ClosedCount);

        List<HTONMapperGaugeChart.SegmentData> SegmentList = new ArrayList<>();
        SegmentList.add(new HTONMapperGaugeChart.SegmentData("Open", OpenCount, HTONMapperTheme.ColorSoftGreen));
        SegmentList.add(new HTONMapperGaugeChart.SegmentData("Filtered", FilteredCount, HTONMapperTheme.ColorSoftYellow));
        SegmentList.add(new HTONMapperGaugeChart.SegmentData("Closed", ClosedCount, HTONMapperTheme.ColorTextMuted));
        PortGaugeChart.SetSegments(SegmentList);

        int TotalCount = OpenCount + FilteredCount + ClosedCount;
        PortGaugeChart.SetCenterText(String.valueOf(TotalCount), "ports scanned");
    }

    private void RefreshChart() {
        UpdateFromResults(0, 0, 0, 0);
    }
}
