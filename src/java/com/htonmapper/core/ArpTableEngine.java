package com.htonmapper.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArpTableEngine {

    private static final Pattern WindowsArpPattern = Pattern.compile(
            "^\\s*(\\d{1,3}(?:\\.\\d{1,3}){3})\\s+([0-9a-fA-F-]{17})\\s+(\\w+)");
    private static final Pattern LinuxArpPattern = Pattern.compile(
            "^\\s*(\\d{1,3}(?:\\.\\d{1,3}){3})\\s+\\S+\\s+\\S+\\s+([0-9a-fA-F:]{17})\\s+\\S+\\s+(\\S+)");

    public void ReadLocalArpTable(Consumer<ArpEntryResult> OnEntryFound, Runnable OnReadComplete, Consumer<String> OnLogMessage) {
        Thread ReadThread = new Thread(() -> {
            String OsNameArg = System.getProperty("os.name", "").toLowerCase();
            OnLogMessage.accept("[*] Reading local ARP table on " + OsNameArg);
            try {
                List<ArpEntryResult> EntryList = OsNameArg.contains("win") ? ReadWindowsArpTable() : ReadLinuxArpTable();
                for (ArpEntryResult EntryArg : EntryList) {
                    OnEntryFound.accept(EntryArg);
                }
                OnLogMessage.accept("[*] ARP table read finished, " + EntryList.size() + " entries found");
            } catch (Exception ExceptionArg) {
                OnLogMessage.accept("[!] Failed to read ARP table: " + ExceptionArg.getClass().getSimpleName());
            }
            OnReadComplete.run();
        });
        ReadThread.setDaemon(true);
        ReadThread.start();
    }

    private List<ArpEntryResult> ReadWindowsArpTable() throws Exception {
        List<ArpEntryResult> ResultList = new ArrayList<>();
        Process ProcessArg = Runtime.getRuntime().exec(new String[]{"arp", "-a"});
        try (BufferedReader ReaderArg = new BufferedReader(new InputStreamReader(ProcessArg.getInputStream()))) {
            String LineArg;
            String CurrentInterface = "unknown";
            while ((LineArg = ReaderArg.readLine()) != null) {
                if (LineArg.toLowerCase().contains("interface")) {
                    CurrentInterface = LineArg.trim();
                    continue;
                }
                Matcher MatcherArg = WindowsArpPattern.matcher(LineArg);
                if (MatcherArg.find()) {
                    ResultList.add(new ArpEntryResult(MatcherArg.group(1), MatcherArg.group(2), CurrentInterface, MatcherArg.group(3)));
                }
            }
        }
        ProcessArg.waitFor();
        return ResultList;
    }

    private List<ArpEntryResult> ReadLinuxArpTable() throws Exception {
        List<ArpEntryResult> ResultList = new ArrayList<>();
        Process ProcessArg;
        try {
            ProcessArg = Runtime.getRuntime().exec(new String[]{"ip", "neigh"});
        } catch (Exception ExceptionArg) {
            ProcessArg = Runtime.getRuntime().exec(new String[]{"arp", "-n"});
        }
        try (BufferedReader ReaderArg = new BufferedReader(new InputStreamReader(ProcessArg.getInputStream()))) {
            String LineArg;
            while ((LineArg = ReaderArg.readLine()) != null) {
                ResultList.add(ParseLinuxLine(LineArg));
            }
        }
        ProcessArg.waitFor();
        ResultList.removeIf(EntryArg -> EntryArg == null);
        return ResultList;
    }

    private ArpEntryResult ParseLinuxLine(String LineArg) {
        String[] TokenList = LineArg.trim().split("\\s+");
        if (TokenList.length < 2) {
            return null;
        }
        String IpAddressArg = TokenList[0];
        if (!IpAddressArg.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
            return null;
        }
        String MacAddressArg = "incomplete";
        String InterfaceArg = "unknown";
        String StateArg = "UNKNOWN";
        for (int IndexArg = 0; IndexArg < TokenList.length; IndexArg++) {
            if (TokenList[IndexArg].equals("dev") && IndexArg + 1 < TokenList.length) {
                InterfaceArg = TokenList[IndexArg + 1];
            }
            if (TokenList[IndexArg].equals("lladdr") && IndexArg + 1 < TokenList.length) {
                MacAddressArg = TokenList[IndexArg + 1];
            }
            if (TokenList[IndexArg].matches("[0-9a-fA-F:]{17}")) {
                MacAddressArg = TokenList[IndexArg];
            }
        }
        StateArg = TokenList[TokenList.length - 1];
        return new ArpEntryResult(IpAddressArg, MacAddressArg, InterfaceArg, StateArg);
    }
}
