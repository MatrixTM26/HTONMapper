package com.htonmapper.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHeaderResult {

    private final int StatusCode;
    private final String ServerBanner;
    private final String PoweredByBanner;
    private final Map<String, String> AllHeaders;
    private final List<String> MissingSecurityHeaders;
    private final List<String> DetectedTechnologies;
    private final boolean HasOutdatedSignature;

    public HttpHeaderResult(int StatusCodeArg, String ServerBannerArg, String PoweredByBannerArg, Map<String, String> AllHeadersArg, List<String> MissingSecurityHeadersArg, List<String> DetectedTechnologiesArg, boolean HasOutdatedSignatureArg) {
        this.StatusCode = StatusCodeArg;
        this.ServerBanner = ServerBannerArg;
        this.PoweredByBanner = PoweredByBannerArg;
        this.AllHeaders = AllHeadersArg;
        this.MissingSecurityHeaders = MissingSecurityHeadersArg == null ? new ArrayList<>() : MissingSecurityHeadersArg;
        this.DetectedTechnologies = DetectedTechnologiesArg == null ? new ArrayList<>() : DetectedTechnologiesArg;
        this.HasOutdatedSignature = HasOutdatedSignatureArg;
    }

    public int GetStatusCode() {
        return StatusCode;
    }

    public String GetServerBanner() {
        return ServerBanner;
    }

    public String GetPoweredByBanner() {
        return PoweredByBanner;
    }

    public Map<String, String> GetAllHeaders() {
        return AllHeaders;
    }

    public List<String> GetMissingSecurityHeaders() {
        return MissingSecurityHeaders;
    }

    public List<String> GetDetectedTechnologies() {
        return DetectedTechnologies;
    }

    public boolean GetHasOutdatedSignature() {
        return HasOutdatedSignature;
    }
}
