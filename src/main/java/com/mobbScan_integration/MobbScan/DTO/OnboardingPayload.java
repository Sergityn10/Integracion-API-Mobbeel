package com.mobbScan_integration.MobbScan.DTO;


import java.util.Map;

public class OnboardingPayload {
    private String countryId;
    private String docType;
    private String redirectUrl;
    private String scanId;
    private Map<String, String> verificationExtraData;

    // Getters y setters
    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public String getScanId() { return scanId; }
    public void setScanId(String scanId) { this.scanId = scanId; }

    public Map<String, String> getVerificationExtraData() { return verificationExtraData; }
    public void setVerificationExtraData(Map<String, String> verificationExtraData) { this.verificationExtraData = verificationExtraData; }
}

