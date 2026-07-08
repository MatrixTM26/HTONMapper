package com.htonmapper.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class DnsLookupEngine {

    private static final String[] QueryTypes = { "A", "AAAA", "MX", "TXT", "NS", "CNAME", "SOA" };

    public void ResolveAllRecords(String HostName, Consumer<DnsRecordResult> OnRecordFound, Runnable OnLookupComplete, Consumer<String> OnLogMessage) {
        Thread LookupThread = new Thread(() -> {
            OnLogMessage.accept("[*] Resolving DNS records for " + HostName);
            for (String QueryTypeArg : QueryTypes) {
                ResolveSingleType(HostName, QueryTypeArg, OnRecordFound, OnLogMessage);
            }
            OnLogMessage.accept("[*] DNS record resolution finished");
            OnLookupComplete.run();
        });
        LookupThread.setDaemon(true);
        LookupThread.start();
    }

    private void ResolveSingleType(String HostName, String QueryTypeArg, Consumer<DnsRecordResult> OnRecordFound, Consumer<String> OnLogMessage) {
        long StartTimeMs = System.currentTimeMillis();
        try {
            if ("A".equals(QueryTypeArg) || "AAAA".equals(QueryTypeArg)) {
                ResolveAddressRecords(HostName, QueryTypeArg, OnRecordFound, StartTimeMs);
                return;
            }

            Hashtable<String, String> EnvironmentArg = new Hashtable<>();
            EnvironmentArg.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            InitialDirContext DirContextArg = new InitialDirContext(EnvironmentArg);
            Attributes AttributesArg = DirContextArg.getAttributes(HostName, new String[] { QueryTypeArg });
            Attribute AttributeArg = AttributesArg.get(QueryTypeArg);

            if (AttributeArg == null) {
                return;
            }
            long QueryTimeMs = System.currentTimeMillis() - StartTimeMs;
            for (int IndexArg = 0; IndexArg < AttributeArg.size(); IndexArg++) {
                String RecordValueArg = String.valueOf(AttributeArg.get(IndexArg));
                OnRecordFound.accept(new DnsRecordResult(QueryTypeArg, RecordValueArg, QueryTimeMs));
            }
        } catch (Exception ExceptionArg) {
            /* record type not present for this host, skip silently */
        }
    }

    private void ResolveAddressRecords(String HostName, String QueryTypeArg, Consumer<DnsRecordResult> OnRecordFound, long StartTimeMs) {
        try {
            InetAddress[] AddressList = InetAddress.getAllByName(HostName);
            long QueryTimeMs = System.currentTimeMillis() - StartTimeMs;
            for (InetAddress AddressArg : AddressList) {
                boolean IsIpv6 = AddressArg.getHostAddress().contains(":");
                if ("AAAA".equals(QueryTypeArg) && IsIpv6) {
                    OnRecordFound.accept(new DnsRecordResult("AAAA", AddressArg.getHostAddress(), QueryTimeMs));
                } else if ("A".equals(QueryTypeArg) && !IsIpv6) {
                    OnRecordFound.accept(new DnsRecordResult("A", AddressArg.getHostAddress(), QueryTimeMs));
                }
            }
        } catch (Exception ExceptionArg) {
            /* host does not resolve for this address family */
        }
    }

    public List<String> ResolveIpAddressesOnly(String HostName) {
        List<String> ResultList = new ArrayList<>();
        try {
            InetAddress[] AddressList = InetAddress.getAllByName(HostName);
            for (InetAddress AddressArg : AddressList) {
                ResultList.add(AddressArg.getHostAddress());
            }
        } catch (Exception ExceptionArg) {
            /* unresolved host, return empty list */
        }
        return ResultList;
    }
}
