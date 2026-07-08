package com.htonmapper.core;

public class SslCertificateResult {

    private final String SubjectName;
    private final String IssuerName;
    private final String ValidFromDate;
    private final String ValidUntilDate;
    private final String SignatureAlgorithm;
    private final String ProtocolVersion;
    private final String CipherSuiteName;
    private final int DaysUntilExpiry;
    private final boolean IsExpired;
    private final boolean IsSelfSigned;

    public SslCertificateResult(String SubjectNameArg, String IssuerNameArg, String ValidFromDateArg, String ValidUntilDateArg,
                                 String SignatureAlgorithmArg, String ProtocolVersionArg, String CipherSuiteNameArg,
                                 int DaysUntilExpiryArg, boolean IsExpiredArg, boolean IsSelfSignedArg) {
        this.SubjectName = SubjectNameArg;
        this.IssuerName = IssuerNameArg;
        this.ValidFromDate = ValidFromDateArg;
        this.ValidUntilDate = ValidUntilDateArg;
        this.SignatureAlgorithm = SignatureAlgorithmArg;
        this.ProtocolVersion = ProtocolVersionArg;
        this.CipherSuiteName = CipherSuiteNameArg;
        this.DaysUntilExpiry = DaysUntilExpiryArg;
        this.IsExpired = IsExpiredArg;
        this.IsSelfSigned = IsSelfSignedArg;
    }

    public String GetSubjectName() {
        return SubjectName;
    }

    public String GetIssuerName() {
        return IssuerName;
    }

    public String GetValidFromDate() {
        return ValidFromDate;
    }

    public String GetValidUntilDate() {
        return ValidUntilDate;
    }

    public String GetSignatureAlgorithm() {
        return SignatureAlgorithm;
    }

    public String GetProtocolVersion() {
        return ProtocolVersion;
    }

    public String GetCipherSuiteName() {
        return CipherSuiteName;
    }

    public int GetDaysUntilExpiry() {
        return DaysUntilExpiry;
    }

    public boolean GetIsExpired() {
        return IsExpired;
    }

    public boolean GetIsSelfSigned() {
        return IsSelfSigned;
    }
}
