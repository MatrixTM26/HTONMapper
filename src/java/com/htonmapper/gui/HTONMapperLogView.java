package com.htonmapper.gui;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTONMapperLogView extends HTONMapperPanel {

    private JTextArea LogTextArea;
    private final SimpleDateFormat TimeFormatter = new SimpleDateFormat("HH:mm:ss");

    public HTONMapperLogView() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        HTONMapperLabel TitleLabel = new HTONMapperLabel("Activity Log");
        TitleLabel.SetHeadingStyle();
        TitleLabel.setBorder(new EmptyBorder(0, 0, 14, 0));

        LogTextArea = new JTextArea();
        LogTextArea.setEditable(false);
        LogTextArea.setFont(HTONMapperTheme.FontMono);
        LogTextArea.setBackground(HTONMapperTheme.ColorBackgroundInset);
        LogTextArea.setForeground(HTONMapperTheme.ColorSoftGreen);
        LogTextArea.setCaretColor(HTONMapperTheme.ColorSoftBlue);
        LogTextArea.setLineWrap(true);
        LogTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane ScrollContainer = new JScrollPane(LogTextArea);
        ScrollContainer.setBorder(new LineBorder(HTONMapperTheme.ColorBorderDefault, 1));

        add(TitleLabel, BorderLayout.NORTH);
        add(ScrollContainer, BorderLayout.CENTER);
    }

    public void AppendLogMessage(String MessageText) {
        SwingUtilities.invokeLater(() -> {
            String TimeStamp = TimeFormatter.format(new Date());
            LogTextArea.append("[" + TimeStamp + "] " + MessageText + "\n");
            LogTextArea.setCaretPosition(LogTextArea.getDocument().getLength());
        });
    }

    public void ClearLog() {
        SwingUtilities.invokeLater(() -> LogTextArea.setText(""));
    }
}
