package com.htonmapper.gui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class HTONMapperGaugeChart extends JPanel {

    public static class SegmentData {
        public final String LabelText;
        public final int ValueAmount;
        public final Color SegmentColor;

        public SegmentData(String LabelTextArg, int ValueAmountArg, Color SegmentColorArg) {
            this.LabelText = LabelTextArg;
            this.ValueAmount = ValueAmountArg;
            this.SegmentColor = SegmentColorArg;
        }
    }

    private final List<SegmentData> SegmentList = new ArrayList<>();
    private String CenterTitleText = "";
    private String CenterSubtitleText = "";

    public HTONMapperGaugeChart() {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(220, 220));
    }

    public void SetSegments(List<SegmentData> NewSegmentList) {
        SegmentList.clear();
        SegmentList.addAll(NewSegmentList);
        repaint();
    }

    public void SetCenterText(String TitleTextArg, String SubtitleTextArg) {
        this.CenterTitleText = TitleTextArg;
        this.CenterSubtitleText = SubtitleTextArg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics GraphicsArg) {
        super.paintComponent(GraphicsArg);
        Graphics2D Graphics2DArg = (Graphics2D) GraphicsArg.create();
        Graphics2DArg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int DiameterSize = Math.min(getWidth(), getHeight()) - 20;
        int OffsetX = (getWidth() - DiameterSize) / 2;
        int OffsetY = (getHeight() - DiameterSize) / 2;
        int StrokeWidth = Math.max(14, DiameterSize / 10);

        int TotalAmount = 0;
        for (SegmentData SegmentArg : SegmentList) {
            TotalAmount += SegmentArg.ValueAmount;
        }

        Graphics2DArg.setStroke(new BasicStroke(StrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        Graphics2DArg.setColor(HTONMapperTheme.ColorBackgroundInset);
        Graphics2DArg.drawArc(OffsetX, OffsetY, DiameterSize, DiameterSize, 0, 360);

        if (TotalAmount > 0) {
            double StartAngle = 90;
            for (SegmentData SegmentArg : SegmentList) {
                double SweepAngle = (SegmentArg.ValueAmount / (double) TotalAmount) * 360.0;
                Graphics2DArg.setColor(SegmentArg.SegmentColor);
                Graphics2DArg.drawArc(OffsetX, OffsetY, DiameterSize, DiameterSize,
                        (int) Math.round(StartAngle), -(int) Math.round(SweepAngle));
                StartAngle -= SweepAngle;
            }
        }

        Graphics2DArg.setColor(HTONMapperTheme.ColorTextPrimary);
        Graphics2DArg.setFont(HTONMapperTheme.FontMonoHeading);
        DrawCenteredString(Graphics2DArg, CenterTitleText, getWidth() / 2, getHeight() / 2 - 6);

        Graphics2DArg.setColor(HTONMapperTheme.ColorTextMuted);
        Graphics2DArg.setFont(HTONMapperTheme.FontMonoSmall);
        DrawCenteredString(Graphics2DArg, CenterSubtitleText, getWidth() / 2, getHeight() / 2 + 14);

        Graphics2DArg.dispose();
    }

    private void DrawCenteredString(Graphics2D Graphics2DArg, String TextArg, int CenterX, int CenterY) {
        if (TextArg == null || TextArg.isEmpty()) {
            return;
        }
        java.awt.FontMetrics MetricsArg = Graphics2DArg.getFontMetrics();
        int TextWidth = MetricsArg.stringWidth(TextArg);
        Graphics2DArg.drawString(TextArg, CenterX - (TextWidth / 2), CenterY);
    }
}
