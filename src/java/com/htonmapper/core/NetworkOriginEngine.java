package com.htonmapper.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkOriginEngine {

    private static final String TorBulkExitListUrl = "https://check.torproject.org/torbulkexitlist";
    private static final Pattern OrgNamePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern CountryPattern = Pattern.compile("\"country\"\\s*:\\s*\"([^\"]+)\"");

    public void AnalyzeOrigin(String TargetHost, int TimeoutMs, Consumer<NetworkOriginResult> OnResultReady, Consumer<String> OnScanFailed, Consumer<String> OnLogMessage) {
        Thread ScanThread = new Thread(() -> {
            OnLogMessage.accept("[*] Resolving IP address for " + TargetHost);
            try {
                String ResolvedIp = InetAddress.getByName(TargetHost).getHostAddress();
                OnLogMessage.accept("[*] Checking Tor exit node list for " + ResolvedIp);
                boolean IsTorExit = CheckTorExitList(ResolvedIp, TimeoutMs);

                OnLogMessage.accept("[*] Querying RDAP registry data for " + ResolvedIp);
                String[] RdapInfo = QueryRdapInfo(ResolvedIp, TimeoutMs);
                String OrganizationName = RdapInfo[0];
                String CountryCode = RdapInfo[1];

                boolean IsHostingRange = DetectHostingKeyword(OrganizationName);
                String SummaryText = BuildSummary(IsTorExit, IsHostingRange, OrganizationName);

                OnResultReady.accept(new NetworkOriginResult(IsTorExit, IsHostingRange, OrganizationName, CountryCode, SummaryText));
                OnLogMessage.accept("[*] Origin analysis completed");
            } catch (Exception ExceptionArg) {
                OnScanFailed.accept(ExceptionArg.getMessage() == null ? ExceptionArg.getClass().getSimpleName() : ExceptionArg.getMessage());
                OnLogMessage.accept("[!] Origin analysis failed: " + ExceptionArg.getClass().getSimpleName());
            }
        });
        ScanThread.setDaemon(true);
        ScanThread.start();
    }

    private boolean CheckTorExitList(String IpAddress, int TimeoutMs) {
        try {
            URL UrlArg = new URL(TorBulkExitListUrl);
            HttpURLConnection ConnectionArg = (HttpURLConnection) UrlArg.openConnection();
            ConnectionArg.setConnectTimeout(TimeoutMs);
            ConnectionArg.setReadTimeout(TimeoutMs);
            ConnectionArg.connect();

            try (BufferedReader ReaderArg = new BufferedReader(new InputStreamReader(ConnectionArg.getInputStream(), StandardCharsets.UTF_8))) {
                String LineArg;
                while ((LineArg = ReaderArg.readLine()) != null) {
                    if (LineArg.trim().equals(IpAddress)) {
                        return true;
                    }
                }
            }
            ConnectionArg.disconnect();
        } catch (Exception ExceptionArg) {
            return false;
        }
        return false;
    }

    private String[] QueryRdapInfo(String IpAddress, int TimeoutMs) {
        String OrganizationName = "Unknown";
        String CountryCode = "Unknown";
        try {
            URL UrlArg = new URL("https://rdap.org/ip/" + IpAddress);
            HttpURLConnection ConnectionArg = (HttpURLConnection) UrlArg.openConnection();
            ConnectionArg.setConnectTimeout(TimeoutMs);
            ConnectionArg.setReadTimeout(TimeoutMs);
            ConnectionArg.setRequestProperty("Accept", "application/rdap+json");
            ConnectionArg.connect();

            StringBuilder ResponseBuilder = new StringBuilder();
            try (BufferedReader ReaderArg = new BufferedReader(new InputStreamReader(ConnectionArg.getInputStream(), StandardCharsets.UTF_8))) {
                String LineArg;
                while ((LineArg = ReaderArg.readLine()) != null) {
                    ResponseBuilder.append(LineArg);
                }
            }
            ConnectionArg.disconnect();

            String ResponseBody = ResponseBuilder.toString();
            Matcher NameMatcher = OrgNamePattern.matcher(ResponseBody);
            if (NameMatcher.find()) {
                OrganizationName = NameMatcher.group(1);
            }
            Matcher CountryMatcher = CountryPattern.matcher(ResponseBody);
            if (CountryMatcher.find()) {
                CountryCode = CountryMatcher.group(1);
            }
        } catch (Exception ExceptionArg) {
            /* rdap lookup failed, return defaults */
        }
        return new String[] { OrganizationName, CountryCode };
    }

    private boolean DetectHostingKeyword(String OrganizationName) {
        String LowerName = OrganizationName.toLowerCase();
        String[] HostingKeywords = { "amazon", "aws", "google", "azure", "digitalocean", "linode", "vultr", "ovh", "hetzner", "cloud", "hosting", "datacenter", "data center", "vpn", "proxy" };
        for (String KeywordArg : HostingKeywords) {
            if (LowerName.contains(KeywordArg)) {
                return true;
            }
        }
        return false;
    }

    private String BuildSummary(boolean IsTorExit, boolean IsHostingRange, String OrganizationName) {
        if (IsTorExit) {
            return "Address matches a published Tor exit node";
        }
        if (IsHostingRange) {
            return "Address belongs to a hosting, cloud, or VPN provider range (" + OrganizationName + ")";
        }
        return "No Tor, VPN, or hosting indicators found";
    }
}
