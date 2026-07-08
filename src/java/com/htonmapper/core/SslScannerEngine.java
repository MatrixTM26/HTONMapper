package com.htonmapper.core;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SslScannerEngine {

    private final SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void ScanCertificate(String TargetHost, int TargetPort, int TimeoutMs,
                                 Consumer<SslCertificateResult> OnCertificateFound,
                                 Consumer<String> OnScanFailed, Consumer<String> OnLogMessage) {
        Thread ScanThread = new Thread(() -> {
            OnLogMessage.accept("[*] Connecting to " + TargetHost + ":" + TargetPort + " for TLS handshake");
            try {
                SSLSocketFactory FactoryArg = (SSLSocketFactory) SSLSocketFactory.getDefault();
                try (SSLSocket SocketArg = (SSLSocket) FactoryArg.createSocket()) {
                    SocketArg.connect(new java.net.InetSocketAddress(TargetHost, TargetPort), TimeoutMs);
                    SocketArg.setSoTimeout(TimeoutMs);
                    SocketArg.startHandshake();

                    SSLSession SessionArg = SocketArg.getSession();
                    Certificate[] CertificateChain = SessionArg.getPeerCertificates();
                    String ProtocolVersion = SessionArg.getProtocol();
                    String CipherSuiteName = SessionArg.getCipherSuite();

                    if (CertificateChain.length == 0) {
                        OnScanFailed.accept("No certificate chain presented by host");
                        return;
                    }

                    X509Certificate LeafCertificate = (X509Certificate) CertificateChain[0];
                    ProcessCertificate(LeafCertificate, CertificateChain, ProtocolVersion, CipherSuiteName, OnCertificateFound);
                    OnLogMessage.accept("[*] TLS handshake completed successfully");
                }
            } catch (Exception ExceptionArg) {
                OnScanFailed.accept(ExceptionArg.getMessage() == null ? ExceptionArg.getClass().getSimpleName() : ExceptionArg.getMessage());
                OnLogMessage.accept("[!] TLS handshake failed: " + ExceptionArg.getClass().getSimpleName());
            }
        });
        ScanThread.setDaemon(true);
        ScanThread.start();
    }

    private void ProcessCertificate(X509Certificate LeafCertificate, Certificate[] CertificateChain,
                                     String ProtocolVersion, String CipherSuiteName, Consumer<SslCertificateResult> OnCertificateFound) {
        String SubjectName = LeafCertificate.getSubjectX500Principal().getName();
        String IssuerName = LeafCertificate.getIssuerX500Principal().getName();
        Date ValidFrom = LeafCertificate.getNotBefore();
        Date ValidUntil = LeafCertificate.getNotAfter();
        String SignatureAlgorithm = LeafCertificate.getSigAlgName();

        long CurrentTimeMs = System.currentTimeMillis();
        long ExpiryTimeMs = ValidUntil.getTime();
        long DaysRemaining = TimeUnit.MILLISECONDS.toDays(ExpiryTimeMs - CurrentTimeMs);
        boolean IsExpired = ExpiryTimeMs < CurrentTimeMs;
        boolean IsSelfSigned = CertificateChain.length == 1 && SubjectName.equals(IssuerName);

        OnCertificateFound.accept(new SslCertificateResult(
                SubjectName,
                IssuerName,
                DateFormatter.format(ValidFrom),
                DateFormatter.format(ValidUntil),
                SignatureAlgorithm,
                ProtocolVersion,
                CipherSuiteName,
                (int) DaysRemaining,
                IsExpired,
                IsSelfSigned
        ));
    }
}
