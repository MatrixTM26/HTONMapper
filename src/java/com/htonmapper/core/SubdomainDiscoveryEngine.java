package com.htonmapper.core;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SubdomainDiscoveryEngine {

    private volatile boolean IsRunning;
    private ExecutorService WorkerPool;

    public SubdomainDiscoveryEngine() {
        this.IsRunning = false;
    }

    public void StartDiscovery(String BaseDomain, int ThreadCount, Consumer<SubdomainResult> OnSubdomainFound, BiConsumer<Integer, Integer> OnProgressUpdate, Runnable OnDiscoveryComplete, Consumer<String> OnLogMessage) {
        IsRunning = true;
        List<String> WordlistArg = SubdomainWordlist.GetDefaultWordlist();
        WorkerPool = Executors.newFixedThreadPool(ThreadCount);
        int TotalWordCount = WordlistArg.size();
        AtomicInteger CompletedCount = new AtomicInteger(0);

        Thread DispatchThread = new Thread(() -> {
            OnLogMessage.accept("[*] Starting subdomain discovery for " + BaseDomain);
            OnLogMessage.accept("[*] Wordlist size: " + TotalWordCount);
            long StartTimeMs = System.currentTimeMillis();

            for (String PrefixArg : WordlistArg) {
                if (!IsRunning) {
                    break;
                }
                WorkerPool.submit(() -> {
                    ResolveSingleSubdomain(PrefixArg, BaseDomain, OnSubdomainFound);
                    int CurrentCompleted = CompletedCount.incrementAndGet();
                    OnProgressUpdate.accept(CurrentCompleted, TotalWordCount);
                });
            }

            WorkerPool.shutdown();
            try {
                WorkerPool.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException ExceptionArg) {
                Thread.currentThread().interrupt();
            }

            long ElapsedMs = System.currentTimeMillis() - StartTimeMs;
            OnLogMessage.accept("[*] Subdomain discovery finished in " + ElapsedMs + " ms");
            IsRunning = false;
            OnDiscoveryComplete.run();
        });
        DispatchThread.setDaemon(true);
        DispatchThread.start();
    }

    private void ResolveSingleSubdomain(String PrefixArg, String BaseDomain, Consumer<SubdomainResult> OnSubdomainFound) {
        String FullSubdomain = PrefixArg + "." + BaseDomain;
        long StartTimeMs = System.currentTimeMillis();
        try {
            InetAddress AddressArg = InetAddress.getByName(FullSubdomain);
            long ResponseTimeMs = System.currentTimeMillis() - StartTimeMs;
            OnSubdomainFound.accept(new SubdomainResult(FullSubdomain, AddressArg.getHostAddress(), ResponseTimeMs));
        } catch (Exception ExceptionArg) {
            /* subdomain does not resolve, skip silently */
        }
    }

    public void StopDiscovery() {
        IsRunning = false;
        if (WorkerPool != null) {
            WorkerPool.shutdownNow();
        }
    }

    public boolean IsDiscoveryRunning() {
        return IsRunning;
    }
}
