package com.htonmapper.core;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScannerEngine {

    private volatile boolean IsRunning;
    private ExecutorService WorkerPool;

    public ScannerEngine() {
        this.IsRunning = false;
    }

    public void StartScan(String TargetHost, int StartPort, int EndPort, int TimeoutMs, int ThreadCount,
                          Consumer<PortResult> OnPortResult, BiConsumer<Integer, Integer> OnProgressUpdate,
                          Runnable OnScanComplete, Consumer<String> OnLogMessage) {
        IsRunning = true;
        WorkerPool = Executors.newFixedThreadPool(ThreadCount);
        int TotalPortCount = (EndPort - StartPort) + 1;
        AtomicInteger CompletedCount = new AtomicInteger(0);

        Thread DispatchThread = new Thread(() -> {
            OnLogMessage.accept("[*] Starting scan on " + TargetHost + " (" + StartPort + "-" + EndPort + ")");
            OnLogMessage.accept("[*] Total ports queued: " + TotalPortCount);
            long StartTimeMs = System.currentTimeMillis();

            for (int CurrentPort = StartPort; CurrentPort <= EndPort; CurrentPort++) {
                if (!IsRunning) {
                    break;
                }
                final int PortToScan = CurrentPort;
                WorkerPool.submit(() -> {
                    ScanSinglePort(TargetHost, PortToScan, TimeoutMs, OnPortResult);
                    int CurrentCompleted = CompletedCount.incrementAndGet();
                    OnProgressUpdate.accept(CurrentCompleted, TotalPortCount);
                });
            }

            WorkerPool.shutdown();
            try {
                WorkerPool.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException ExceptionArg) {
                Thread.currentThread().interrupt();
            }

            long ElapsedMs = System.currentTimeMillis() - StartTimeMs;
            OnLogMessage.accept("[*] Scan finished in " + ElapsedMs + " ms");
            IsRunning = false;
            OnScanComplete.run();
        });
        DispatchThread.setDaemon(true);
        DispatchThread.start();
    }

    private void ScanSinglePort(String TargetHost, int PortNumber, int TimeoutMs, Consumer<PortResult> OnPortResult) {
        long StartTimeMs = System.currentTimeMillis();
        try (Socket SocketArg = new Socket()) {
            SocketArg.connect(new InetSocketAddress(TargetHost, PortNumber), TimeoutMs);
            long ResponseTimeMs = System.currentTimeMillis() - StartTimeMs;
            String ServiceName = ServiceLookup.ResolveServiceName(PortNumber);
            OnPortResult.accept(new PortResult(PortNumber, "OPEN", ServiceName, ResponseTimeMs));
        } catch (Exception ExceptionArg) {
            /* closed or filtered ports are not reported to keep results clean */
        }
    }

    public void StopScan() {
        IsRunning = false;
        if (WorkerPool != null) {
            WorkerPool.shutdownNow();
        }
    }

    public boolean IsScanRunning() {
        return IsRunning;
    }
}
