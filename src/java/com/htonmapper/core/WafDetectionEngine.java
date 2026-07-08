package com.htonmapper.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WafDetectionEngine {

    public void DetectWaf(String TargetUrl, int TimeoutMs, Consumer<WafDetectionResult> OnDetectionResult,
                          Consumer<String> OnScanFailed, Consumer<String> OnLogMessage) {
        Thread ScanThread = new Thread(() -> {
            OnLogMessage.accept("[*] Probing " + TargetUrl + " for WAF signatures");
            try {
                Map<String, String> HeaderMapArg = ProbeWithSuspiciousPayload(TargetUrl, TimeoutMs);
                WafDetectionResult ResultArg = AnalyzeHeaders(HeaderMapArg);
                OnDetectionResult.accept(ResultArg);
                OnLogMessage.accept("[*] WAF probing completed");
            } catch (Exception ExceptionArg) {
                OnScanFailed.accept(ExceptionArg.getMessage() == null ? ExceptionArg.getClass().getSimpleName() : ExceptionArg.getMessage());
                OnLogMessage.accept("[!] WAF probing failed: " + ExceptionArg.getClass().getSimpleName());
            }
        });
        ScanThread.setDaemon(true);
        ScanThread.start();
    }

    private Map<String, String> ProbeWithSuspiciousPayload(String TargetUrl, int TimeoutMs) throws Exception {
        String ProbeUrl = TargetUrl + (TargetUrl.contains("?") ? "&" : "?") + "htonmapper_probe=1";
        URL UrlArg = new URL(ProbeUrl);
        HttpURLConnection ConnectionArg = (HttpURLConnection) UrlArg.openConnection();
        ConnectionArg.setConnectTimeout(TimeoutMs);
        ConnectionArg.setReadTimeout(TimeoutMs);
        ConnectionArg.setRequestProperty("User-Agent", "HTONMapper/1.0 SecurityScanner");
        ConnectionArg.connect();

        Map<String, String> HeaderMapArg = new LinkedHashMap<>();
        Map<String, List<String>> RawHeaderMap = ConnectionArg.getHeaderFields();
        for (Map.Entry<String, List<String>> EntryArg : RawHeaderMap.entrySet()) {
            if (EntryArg.getKey() == null) {
                continue;
            }
            HeaderMapArg.put(EntryArg.getKey(), String.join(", ", EntryArg.getValue()));
        }
        HeaderMapArg.put("HTONMapperStatusCode", String.valueOf(ConnectionArg.getResponseCode()));
        ConnectionArg.disconnect();
        return HeaderMapArg;
    }

    private WafDetectionResult AnalyzeHeaders(Map<String, String> HeaderMapArg) {
        String ServerValue = LowerOrEmpty(HeaderMapArg.get("Server"));
        String SetCookieValue = LowerOrEmpty(HeaderMapArg.get("Set-Cookie"));
        String StatusCodeValue = HeaderMapArg.getOrDefault("HTONMapperStatusCode", "0");

        if (HeaderMapArg.containsKey("CF-RAY") || ServerValue.contains("cloudflare")) {
            return new WafDetectionResult(true, "Cloudflare", "CF-RAY header or Server banner detected");
        }
        if (SetCookieValue.contains("incap_ses") || SetCookieValue.contains("visid_incap")) {
            return new WafDetectionResult(true, "Imperva Incapsula", "Incapsula session cookie detected");
        }
        if (HeaderMapArg.containsKey("X-Sucuri-ID") || ServerValue.contains("sucuri")) {
            return new WafDetectionResult(true, "Sucuri", "Sucuri specific header detected");
        }
        if (HeaderMapArg.containsKey("X-Akamai-Transformed")) {
            return new WafDetectionResult(true, "Akamai", "Akamai transformation header detected");
        }
        if (SetCookieValue.contains("barra_counter_session")) {
            return new WafDetectionResult(true, "Barracuda", "Barracuda session cookie detected");
        }
        if (HeaderMapArg.containsKey("X-Distil-CS")) {
            return new WafDetectionResult(true, "Distil Networks", "Distil header detected");
        }
        if ("406".equals(StatusCodeValue) || "419".equals(StatusCodeValue) || "999".equals(StatusCodeValue)) {
            return new WafDetectionResult(true, "Unknown WAF", "Suspicious status code returned for probe request: " + StatusCodeValue);
        }
        return new WafDetectionResult(false, "None Detected", "No known WAF signature matched");
    }

    private String LowerOrEmpty(String ValueArg) {
        return ValueArg == null ? "" : ValueArg.toLowerCase();
    }
}
