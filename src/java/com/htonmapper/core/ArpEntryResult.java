package com.htonmapper.core;

public class ArpEntryResult {

    private final String IpAddress;
    private final String MacAddress;
    private final String InterfaceName;
    private final String EntryType;

    public ArpEntryResult(String IpAddressArg, String MacAddressArg, String InterfaceNameArg, String EntryTypeArg) {
        this.IpAddress = IpAddressArg;
        this.MacAddress = MacAddressArg;
        this.InterfaceName = InterfaceNameArg;
        this.EntryType = EntryTypeArg;
    }

    public String GetIpAddress() {
        return IpAddress;
    }

    public String GetMacAddress() {
        return MacAddress;
    }

    public String GetInterfaceName() {
        return InterfaceName;
    }

    public String GetEntryType() {
        return EntryType;
    }
}
