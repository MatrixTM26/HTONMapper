package com.htonmapper.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TechScannerEngine {

    private static final String[] RequiredSecurityHeaders = { "Strict-Transport-Security", "Content-Security-Policy", "X-Frame-Options", "X-Content-Type-Options", "Referrer-Policy", "Permissions-Policy" };

    private static final Pattern OutdatedApachePattern = Pattern.compile("Apache/(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final Pattern OutdatedNginxPattern = Pattern.compile("nginx/(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final Pattern OutdatedPhpPattern = Pattern.compile("PHP/(\\d+)\\.(\\d+)");

    public void ScanHeaders(String TargetUrl, int TimeoutMs, Consumer<HttpHeaderResult> OnHeaderResult, Consumer<String> OnScanFailed, Consumer<String> OnLogMessage) {
        Thread ScanThread = new Thread(() -> {
            OnLogMessage.accept("[*] Sending HTTP request to " + TargetUrl);
            HttpURLConnection ConnectionArg = null;
            try {
                URL UrlArg = new URL(TargetUrl);
                ConnectionArg = (HttpURLConnection) UrlArg.openConnection();
                ConnectionArg.setConnectTimeout(TimeoutMs);
                ConnectionArg.setReadTimeout(TimeoutMs);
                ConnectionArg.setInstanceFollowRedirects(true);
                ConnectionArg.setRequestProperty("User-Agent", "HTONMapper/1.0 SecurityScanner");
                ConnectionArg.connect();

                int StatusCode = ConnectionArg.getResponseCode();
                Map<String, String> HeaderMapArg = ExtractHeaders(ConnectionArg);
                String ServerBanner = HeaderMapArg.getOrDefault("Server", "Not disclosed");
                String PoweredByBanner = HeaderMapArg.getOrDefault("X-Powered-By", "Not disclosed");

                List<String> MissingHeadersList = FindMissingSecurityHeaders(HeaderMapArg);
                List<String> TechnologyList = DetectTechnologies(HeaderMapArg);
                boolean HasOutdatedSignature = DetectOutdatedSignature(ServerBanner);

                OnHeaderResult.accept(new HttpHeaderResult(StatusCode, ServerBanner, PoweredByBanner, HeaderMapArg, MissingHeadersList, TechnologyList, HasOutdatedSignature));
                OnLogMessage.accept("[*] HTTP scan completed with status " + StatusCode);
            } catch (Exception ExceptionArg) {
                OnScanFailed.accept(ExceptionArg.getMessage() == null ? ExceptionArg.getClass().getSimpleName() : ExceptionArg.getMessage());
                OnLogMessage.accept("[!] HTTP scan failed: " + ExceptionArg.getClass().getSimpleName());
            } finally {
                if (ConnectionArg != null) {
                    ConnectionArg.disconnect();
                }
            }
        });
        ScanThread.setDaemon(true);
        ScanThread.start();
    }

    private Map<String, String> ExtractHeaders(HttpURLConnection ConnectionArg) {
        Map<String, String> HeaderMapArg = new LinkedHashMap<>();
        Map<String, List<String>> RawHeaderMap = ConnectionArg.getHeaderFields();
        for (Map.Entry<String, List<String>> EntryArg : RawHeaderMap.entrySet()) {
            if (EntryArg.getKey() == null) {
                continue;
            }
            HeaderMapArg.put(EntryArg.getKey(), String.join(", ", EntryArg.getValue()));
        }
        return HeaderMapArg;
    }

    private List<String> FindMissingSecurityHeaders(Map<String, String> HeaderMapArg) {
        List<String> MissingList = new ArrayList<>();
        for (String RequiredHeaderArg : RequiredSecurityHeaders) {
            boolean FoundHeader = false;
            for (String ActualHeaderArg : HeaderMapArg.keySet()) {
                if (ActualHeaderArg.equalsIgnoreCase(RequiredHeaderArg)) {
                    FoundHeader = true;
                    break;
                }
            }
            if (!FoundHeader) {
                MissingList.add(RequiredHeaderArg);
            }
        }
        return MissingList;
    }

    private List<String> DetectTechnologies(Map<String, String> HeaderMapArg) {
        List<String> TechnologyList = new ArrayList<>();
        String ServerValue = HeaderMapArg.getOrDefault("Server", "");
        String PoweredByValue = HeaderMapArg.getOrDefault("X-Powered-By", "");

        if (ServerValue.toLowerCase().contains("nginx")) {
            TechnologyList.add("Nginx");
        }
        if (ServerValue.toLowerCase().contains("apache")) {
            TechnologyList.add("Apache HTTP Server");
        }
        if (ServerValue.toLowerCase().contains("iis") || ServerValue.toLowerCase().contains("microsoft")) {
            TechnologyList.add("Microsoft IIS");
        }
        if (ServerValue.toLowerCase().contains("cloudflare")) {
            TechnologyList.add("Cloudflare");
        }
        if (PoweredByValue.toLowerCase().contains("php")) {
            TechnologyList.add("PHP");
        }
        if (PoweredByValue.toLowerCase().contains("asp.net")) {
            TechnologyList.add("ASP.NET");
        }
        if (HeaderMapArg.containsKey("X-Drupal-Cache")) {
            TechnologyList.add("Drupal");
        }
        if (HeaderMapArg.containsKey("X-Generator") && HeaderMapArg.get("X-Generator").toLowerCase().contains("wordpress")) {
            TechnologyList.add("WordPress");
        }
        if (HeaderMapArg.containsKey("Set-Cookie") && HeaderMapArg.get("Set-Cookie").toLowerCase().contains("laravel")) {
            TechnologyList.add("Laravel");
        }
        if (TechnologyList.isEmpty()) {
            TechnologyList.add("Unidentified");
        }
        return TechnologyList;
    }

    private boolean DetectOutdatedSignature(String ServerBanner) {
        Matcher ApacheMatcher = OutdatedApachePattern.matcher(ServerBanner);
        if (ApacheMatcher.find()) {
            int MajorVersion = Integer.parseInt(ApacheMatcher.group(1));
            int MinorVersion = Integer.parseInt(ApacheMatcher.group(2));
            return MajorVersion < 2 || (MajorVersion == 2 && MinorVersion < 4);
        }

        Matcher NginxMatcher = OutdatedNginxPattern.matcher(ServerBanner);
        if (NginxMatcher.find()) {
            int MajorVersion = Integer.parseInt(NginxMatcher.group(1));
            int MinorVersion = Integer.parseInt(NginxMatcher.group(2));
            return MajorVersion < 1 || (MajorVersion == 1 && MinorVersion < 18);
        }

        Matcher PhpMatcher = OutdatedPhpPattern.matcher(ServerBanner);
        if (PhpMatcher.find()) {
            int MajorVersion = Integer.parseInt(PhpMatcher.group(1));
            int MinorVersion = Integer.parseInt(PhpMatcher.group(2));
            return MajorVersion < 7 || (MajorVersion == 7 && MinorVersion < 4);
        }

        return false;
    }
}
