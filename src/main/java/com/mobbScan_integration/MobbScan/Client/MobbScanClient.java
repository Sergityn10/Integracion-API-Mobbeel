package com.mobbScan_integration.MobbScan.Client;


import com.mobbScan_integration.MobbScan.DTO.AcceptRequest;
import com.mobbScan_integration.MobbScan.DTO.PendingRequest;
import com.mobbScan_integration.MobbScan.DTO.RejectRequest;
import com.mobbScan_integration.MobbScan.OnboardingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "mobbscanClient", url = "${mobbscan.api.gateway}")
public interface MobbScanClient {

    @PostMapping(value = "/auth/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> authenticate(@RequestBody Map<String, String> authRequest);

    @PostMapping(value = "/onboarding/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> doOnboarding(@RequestHeader("Authorization") String bearerToken,
                                     @RequestBody OnboardingRequest request);

    @GetMapping(value = "/mobbscan-agent/getVerificationProcessData/{verificationId}")
    Map<String, Object> getVerificationData(@RequestHeader("Authorization") String bearerToken,
                                            @PathVariable String verificationId);

    @GetMapping(value = "/mobbscan-agent/checkVerificationProcessResult/{verificationId}")
    Map<String, Object> checkVerificationResult(@RequestHeader("Authorization") String bearerToken,
                                                @PathVariable String verificationId);
    @GetMapping(value = "/mobbscan-agent/{verificationId}/image/{imageType}", produces = MediaType.ALL_VALUE)
    ResponseEntity<byte[]> getVerificationImage(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String verificationId,
            @PathVariable String imageType,
            @RequestHeader("Accept") String accept
    );

    @GetMapping(value = "/mobbscan-agent/{verificationId}/recording/{recordingType}", produces = MediaType.ALL_VALUE)
    ResponseEntity<byte[]> getRecording(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String verificationId,
            @PathVariable String recordingType,
            @RequestHeader("Accept") String accept
    );

    @GetMapping(value = "/api/zip/{verificationId}", produces = MediaType.ALL_VALUE)
    ResponseEntity<byte[]> getZip(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String verificationId,
            @RequestHeader("Accept") String accept
    );

    @PostMapping(value = "/mobbscan-agent/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> sendToAccept(@RequestHeader("Authorization") String bearerToken,
                                     @RequestBody AcceptRequest request);

    @PostMapping(value = "/mobbscan-agent/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> sendToReject(@RequestHeader("Authorization") String bearerToken,
                                     @RequestBody RejectRequest request);

    @PostMapping(value = "/mobbscan-agent/pending", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> sendToPending(@RequestHeader("Authorization") String bearerToken,
                                      @RequestBody PendingRequest request);

    @DeleteMapping(value = "/mobbscan-agent/api/verification/{verificationId}")
    void deleteVerification(@RequestHeader("Authorization") String bearerToken,
                            @PathVariable String verificationId);
}
