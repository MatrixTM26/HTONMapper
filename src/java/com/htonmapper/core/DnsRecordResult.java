package com.htonmapper.core;

public class DnsRecordResult {

    private final String RecordType;
    private final String RecordValue;
    private final long QueryTimeMs;

    public DnsRecordResult(String RecordTypeArg, String RecordValueArg, long QueryTimeMsArg) {
        this.RecordType = RecordTypeArg;
        this.RecordValue = RecordValueArg;
        this.QueryTimeMs = QueryTimeMsArg;
    }

    public String GetRecordType() {
        return RecordType;
    }

    public String GetRecordValue() {
        return RecordValue;
    }

    public long GetQueryTimeMs() {
        return QueryTimeMs;
    }
}
