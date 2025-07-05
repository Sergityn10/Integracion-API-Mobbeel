package com.mobbScan_integration.MobbScan.controller;


import com.mobbScan_integration.MobbScan.DTO.request.AuthRequest;
import com.mobbScan_integration.MobbScan.Client.MobbScanClient;
import com.mobbScan_integration.MobbScan.DTO.request.AcceptRequest;
import com.mobbScan_integration.MobbScan.DTO.request.PendingRequest;
import com.mobbScan_integration.MobbScan.DTO.request.RejectRequest;

import com.mobbScan_integration.MobbScan.DTO.request.OnboardingRequest;
import feign.RequestInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final MobbScanClient mobbScanClient;

    @Value("${mobbscan.api.key}")
    private String apiKey;

    @Value("${mobbscan.api.secret}")
    private String apiSecret;

    @Value("${mobbscan.api.gateway}")
    private String gateway;

    private String accessToken = "";

    @PostConstruct
    public void init() {
        refreshAccessToken();
    }

    @Bean
    public RequestInterceptor mobbScanAuthInterceptor() {
        return template -> {
            if (accessToken != null && !accessToken.isEmpty()) {
                template.header("Authorization", "Bearer " + accessToken);
            }
        };
    }

    private ResponseEntity<Map> tryOnboarding(OnboardingRequest onboardingRequest, String token) {
        String url = gateway + "/onboarding/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);


        HttpEntity<OnboardingRequest> request = new HttpEntity<>(onboardingRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }

    private boolean refreshAccessToken() {
        try {
            String authUrl = gateway + "/auth/token";

            Map<String, String> authBody = Map.of(
                    "api_key", apiKey,
                    "api_secret", apiSecret
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, String>> authRequest = new HttpEntity<>(authBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(authUrl, HttpMethod.POST, authRequest, Map.class);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().get("accessToken") != null) {
                this.accessToken = response.getBody().get("accessToken").toString();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error al refrescar token: " + e.getMessage());
        }
        return false;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> doAuthentication(@RequestBody AuthRequest authRequest) {
        Map<String, String> body = Map.of("api_key", authRequest.getApi_key(), "api_secret", authRequest.getApi_secret());
        try {
            Map<String, Object> response = mobbScanClient.authenticate(body);
            if (response.containsKey("accessToken")) {
                this.accessToken = response.get("accessToken").toString();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> doOnboarding(@RequestBody OnboardingRequest onboardingRequest) {
        try {
            Map<String, Object> response = mobbScanClient.doOnboarding("Bearer " + accessToken, onboardingRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (refreshAccessToken()) {
                try {
                    Map<String, Object> response = mobbScanClient.doOnboarding("Bearer " + accessToken, onboardingRequest);
                    return ResponseEntity.ok(response);
                } catch (Exception retryEx) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Retry failed: " + retryEx.getMessage()));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/getVerificationProcessData/{verificationId}")
    public ResponseEntity<?> getVerificationProcessData(@PathVariable String verificationId) {
        try {
            Map<String, Object> response = mobbScanClient.getVerificationData("Bearer " + accessToken, verificationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/checkVerificationProcessResult/{verificationId}")
    public ResponseEntity<?> checkVerificationProcessResult(@PathVariable String verificationId) {
        try {
            Map<String, Object> response = mobbScanClient.checkVerificationResult("Bearer " + accessToken, verificationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getImage/{verificationId}/{imageType}")
    public ResponseEntity<byte[]> getVerificationImage(@PathVariable String verificationId, @PathVariable String imageType, @RequestHeader(value = "Accept", defaultValue = "application/json") String accept) {
        try {
            return mobbScanClient.getVerificationImage("Bearer " + accessToken, verificationId, imageType, accept);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/getRecording/{verificationId}/{recordingType}")
    public ResponseEntity<byte[]> getRecording(@PathVariable String verificationId, @PathVariable String recordingType, @RequestHeader(value = "Accept", defaultValue = "application/json") String accept) {
        try {
            return mobbScanClient.getRecording("Bearer " + accessToken, verificationId, recordingType, accept);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/getZip/{verificationId}")
    public ResponseEntity<byte[]> getZip(@PathVariable String verificationId, @RequestHeader(value = "Accept", defaultValue = "application/json") String accept) {
        try {
            return mobbScanClient.getZip("Bearer " + accessToken, verificationId, accept);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> sendToAccept(@RequestBody AcceptRequest acceptRequest) {
        try {
            Map<String, Object> response = mobbScanClient.sendToAccept("Bearer " + accessToken, acceptRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> sendToReject(@RequestBody RejectRequest rejectRequest) {
        try {
            Map<String, Object> response = mobbScanClient.sendToReject("Bearer " + accessToken, rejectRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pending")
    public ResponseEntity<?> sendToPending(@RequestBody PendingRequest pendingRequest) {
        try {
            Map<String, Object> response = mobbScanClient.sendToPending("Bearer " + accessToken, pendingRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{verificationId}")
    public ResponseEntity<?> deleteVerification(@PathVariable String verificationId) {
        try {
            mobbScanClient.deleteVerification("Bearer " + accessToken, verificationId);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }



}
