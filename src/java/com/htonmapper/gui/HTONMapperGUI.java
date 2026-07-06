package com.htonmapper.gui;

import com.htonmapper.core.ScannerEngine;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class HTONMapperGUI {

    private final HTONMapperFrame FrameInstance;
    private final ScannerEngine EngineInstance;
    private HTONMapperScannerView ScannerViewInstance;
    private HTONMapperResultsView ResultsViewInstance;
    private HTONMapperLogView LogViewInstance;
    private HTONMapperAboutView AboutViewInstance;
    private CardLayout ContentCardLayout;
    private JPanel ContentContainer;

    public HTONMapperGUI() {
        this.FrameInstance = new HTONMapperFrame("HTONMapper");
        this.EngineInstance = new ScannerEngine();
        ConfigureLayout();
    }

    private void ConfigureLayout() {
        HTONMapperTitleBar TitleBarInstance = new HTONMapperTitleBar("HTONMapper");

        ContentCardLayout = new CardLayout();
        ContentContainer = new JPanel(ContentCardLayout);
        ContentContainer.setBackground(HTONMapperTheme.ColorBackgroundPanel);

        ScannerViewInstance = new HTONMapperScannerView(this::HandleStartScan, this::HandleStopScan);
        ResultsViewInstance = new HTONMapperResultsView();
        LogViewInstance = new HTONMapperLogView();
        AboutViewInstance = new HTONMapperAboutView();

        ContentContainer.add(ScannerViewInstance, "Scanner");
        ContentContainer.add(ResultsViewInstance, "Results");
        ContentContainer.add(LogViewInstance, "Log");
        ContentContainer.add(AboutViewInstance, "About");

        HTONMapperSidebar SidebarInstance = new HTONMapperSidebar(this::HandleNavigate);

        JPanel BodyPanel = new JPanel(new BorderLayout());
        BodyPanel.add(SidebarInstance, BorderLayout.WEST);
        BodyPanel.add(ContentContainer, BorderLayout.CENTER);

        FrameInstance.setLayout(new BorderLayout());
        FrameInstance.add(TitleBarInstance, BorderLayout.NORTH);
        FrameInstance.add(BodyPanel, BorderLayout.CENTER);
    }

    private void HandleNavigate(String TargetSection) {
        ContentCardLayout.show(ContentContainer, TargetSection);
    }

    private void HandleStartScan(String HostValue, int[] ParamsArg) {
        int StartPortValue = ParamsArg[0];
        int EndPortValue = ParamsArg[1];
        int TimeoutValue = ParamsArg[2];
        int ThreadValue = ParamsArg[3];

        ResultsViewInstance.ClearResults();
        LogViewInstance.ClearLog();
        HandleNavigate("Log");

        EngineInstance.StartScan(
                HostValue,
                StartPortValue,
                EndPortValue,
                TimeoutValue,
                ThreadValue,
                ResultsViewInstance::AddResultRow,
                ScannerViewInstance.GetProgressIndicator()::UpdatePercentValue,
                this::HandleScanComplete,
                LogViewInstance::AppendLogMessage
        );
    }

    private void HandleStopScan() {
        EngineInstance.StopScan();
        LogViewInstance.AppendLogMessage("[!] Scan stopped by user");
    }

    private void HandleScanComplete() {
        ScannerViewInstance.SetScanningState(false);
    }

    public void LaunchApplication() {
        FrameInstance.setVisible(true);
    }
}
