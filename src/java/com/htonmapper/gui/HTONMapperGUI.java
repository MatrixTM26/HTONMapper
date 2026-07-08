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
    private HTONMapperIpHostnameView IpHostnameViewInstance;
    private HTONMapperDnsRecordsView DnsRecordsViewInstance;
    private HTONMapperSubdomainsView SubdomainsViewInstance;
    private HTONMapperSslView SslViewInstance;
    private HTONMapperTechStackView TechStackViewInstance;
    private HTONMapperWafView WafViewInstance;
    private HTONMapperOriginView OriginViewInstance;
    private HTONMapperArpView ArpViewInstance;
    private HTONMapperDashboardView DashboardViewInstance;
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
        IpHostnameViewInstance = new HTONMapperIpHostnameView(this::AppendLog);
        DnsRecordsViewInstance = new HTONMapperDnsRecordsView(this::AppendLog);
        SubdomainsViewInstance = new HTONMapperSubdomainsView(this::AppendLog);
        SslViewInstance = new HTONMapperSslView(this::AppendLog);
        TechStackViewInstance = new HTONMapperTechStackView(this::AppendLog);
        WafViewInstance = new HTONMapperWafView(this::AppendLog);
        OriginViewInstance = new HTONMapperOriginView(this::AppendLog);
        ArpViewInstance = new HTONMapperArpView(this::AppendLog);
        DashboardViewInstance = new HTONMapperDashboardView();
        LogViewInstance = new HTONMapperLogView();
        AboutViewInstance = new HTONMapperAboutView();

        ContentContainer.add(ScannerViewInstance, "Scanner");
        ContentContainer.add(ResultsViewInstance, "Results");
        ContentContainer.add(IpHostnameViewInstance, "IP / Hostname");
        ContentContainer.add(DnsRecordsViewInstance, "DNS Records");
        ContentContainer.add(SubdomainsViewInstance, "Subdomains");
        ContentContainer.add(SslViewInstance, "SSL / TLS");
        ContentContainer.add(TechStackViewInstance, "Tech Stack");
        ContentContainer.add(WafViewInstance, "WAF Detect");
        ContentContainer.add(OriginViewInstance, "Origin Check");
        ContentContainer.add(ArpViewInstance, "ARP Table");
        ContentContainer.add(DashboardViewInstance, "Dashboard");
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

    private void AppendLog(String MessageText) {
        LogViewInstance.AppendLogMessage(MessageText);
    }

    private void HandleStartScan(String HostValue, int[] ParamsArg, boolean IncludeClosedPorts) {
        int StartPortValue = ParamsArg[0];
        int EndPortValue = ParamsArg[1];
        int TimeoutValue = ParamsArg[2];
        int ThreadValue = ParamsArg[3];

        ResultsViewInstance.ClearResults();
        LogViewInstance.ClearLog();

        EngineInstance.StartScan(HostValue, StartPortValue, EndPortValue, TimeoutValue, ThreadValue, IncludeClosedPorts, ResultsViewInstance::AddResultRow, ScannerViewInstance.GetProgressIndicator()::UpdatePercentValue, this::HandleScanComplete, LogViewInstance::AppendLogMessage);
    }

    private void HandleStopScan() {
        EngineInstance.StopScan();
        LogViewInstance.AppendLogMessage("[!] Scan stopped by user");
    }

    private void HandleScanComplete() {
        ScannerViewInstance.SetScanningState(false);
        DashboardViewInstance.UpdateFromResults(ResultsViewInstance.GetOpenCount(), ResultsViewInstance.GetFilteredCount(), ResultsViewInstance.GetClosedCount(), 0);
    }

    public void LaunchApplication() {
        FrameInstance.setVisible(true);
    }
}
