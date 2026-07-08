package com.htonmapper.core;

public class PortResult {

    public static final String StatusOpen = "OPEN";
    public static final String StatusClosed = "CLOSED";
    public static final String StatusFiltered = "FILTERED";

    private final int PortNumber;
    private final String PortStatus;
    private final String ServiceName;
    private final long ResponseTimeMs;

    public PortResult(int PortNumberArg, String PortStatusArg, String ServiceNameArg, long ResponseTimeMsArg) {
        this.PortNumber = PortNumberArg;
        this.PortStatus = PortStatusArg;
        this.ServiceName = ServiceNameArg;
        this.ResponseTimeMs = ResponseTimeMsArg;
    }

    public int GetPortNumber() {
        return PortNumber;
    }

    public String GetPortStatus() {
        return PortStatus;
    }

    public String GetServiceName() {
        return ServiceName;
    }

    public long GetResponseTimeMs() {
        return ResponseTimeMs;
    }
}
