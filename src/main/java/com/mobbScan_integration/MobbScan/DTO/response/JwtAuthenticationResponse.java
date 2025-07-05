package com.mobbScan_integration.MobbScan.DTO.response;


public record JwtAuthenticationResponse(String accessToken, String tokenType) {
    public JwtAuthenticationResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
