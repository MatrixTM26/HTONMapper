package com.htonmapper.core;

public class WafDetectionResult {

    private final boolean IsWafDetected;
    private final String WafVendorName;
    private final String DetectionReason;

    public WafDetectionResult(boolean IsWafDetectedArg, String WafVendorNameArg, String DetectionReasonArg) {
        this.IsWafDetected = IsWafDetectedArg;
        this.WafVendorName = WafVendorNameArg;
        this.DetectionReason = DetectionReasonArg;
    }

    public boolean GetIsWafDetected() {
        return IsWafDetected;
    }

    public String GetWafVendorName() {
        return WafVendorName;
    }

    public String GetDetectionReason() {
        return DetectionReason;
    }
}
