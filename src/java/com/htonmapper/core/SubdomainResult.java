package com.htonmapper.core;

public class SubdomainResult {

    private final String SubdomainName;
    private final String ResolvedIpAddress;
    private final long ResponseTimeMs;

    public SubdomainResult(String SubdomainNameArg, String ResolvedIpAddressArg, long ResponseTimeMsArg) {
        this.SubdomainName = SubdomainNameArg;
        this.ResolvedIpAddress = ResolvedIpAddressArg;
        this.ResponseTimeMs = ResponseTimeMsArg;
    }

    public String GetSubdomainName() {
        return SubdomainName;
    }

    public String GetResolvedIpAddress() {
        return ResolvedIpAddress;
    }

    public long GetResponseTimeMs() {
        return ResponseTimeMs;
    }
}
