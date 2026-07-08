package com.htonmapper.core;

public class NetworkOriginResult {

    private final boolean IsTorExitNode;
    private final boolean IsKnownHostingRange;
    private final String OrganizationName;
    private final String CountryCode;
    private final String DetectionSummary;

    public NetworkOriginResult(boolean IsTorExitNodeArg, boolean IsKnownHostingRangeArg, String OrganizationNameArg, String CountryCodeArg, String DetectionSummaryArg) {
        this.IsTorExitNode = IsTorExitNodeArg;
        this.IsKnownHostingRange = IsKnownHostingRangeArg;
        this.OrganizationName = OrganizationNameArg;
        this.CountryCode = CountryCodeArg;
        this.DetectionSummary = DetectionSummaryArg;
    }

    public boolean GetIsTorExitNode() {
        return IsTorExitNode;
    }

    public boolean GetIsKnownHostingRange() {
        return IsKnownHostingRange;
    }

    public String GetOrganizationName() {
        return OrganizationName;
    }

    public String GetCountryCode() {
        return CountryCode;
    }

    public String GetDetectionSummary() {
        return DetectionSummary;
    }
}
