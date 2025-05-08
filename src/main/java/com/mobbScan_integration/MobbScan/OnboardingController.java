package com.mobbScan_integration.MobbScan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mobbScan_integration.MobbScan.DTO.OnboardingPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/onboarding")
public class OnboardingController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mobbscan.api.key}")
    private String apiKey;

    @Value("${mobbscan.api.secret}")
    private String apiSecret;

    @Value("${mobbscan.api.gateway}")
    private String gateway;

    private String accessToken = "";


    @PostMapping("/authenticate")
    public ResponseEntity<Map> doAuthentication(@RequestBody AuthRequest authRequest) {
        String url = gateway + "/auth/token"; // Asegúrate de que `gateway` tenga el esquema completo https://{GATEWAY-HOST}

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Crea el cuerpo JSON tal como el curl lo hace (ya lo tienes con el DTO)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("api_key", authRequest.getApi_key());
        requestBody.put("api_secret", authRequest.getApi_secret());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Guardar access_token si está presente
        if (response.getBody() != null && response.getBody().get("accessToken") != null) {
            this.accessToken = response.getBody().get("accessToken").toString();
        }

        return response;
    }

    @PostMapping("/create")
    public ResponseEntity<Map> doOnboarding(
            @RequestBody OnboardingRequest onboardingRequest
    ) {

        String url = gateway + "/onboarding/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Si se proporciona el header Authorization, úsalo. Si no, usa el accessToken almacenado.
        System.out.println("ACCESS TOKEN: " + accessToken);
        if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }



        HttpEntity<OnboardingRequest> request = new HttpEntity<>(onboardingRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return response;
        } catch (HttpClientErrorException e) {
            System.out.println("ERROR BODY: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        }


    }


    @GetMapping("/getVerificationProcessData/{verificationId}")
    public ResponseEntity<Map> getVerificationProcessData(
            @PathVariable String verificationId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String url = gateway + "/mobbscan-agent/getVerificationProcessData/" + verificationId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        return response;
    }

    @GetMapping("/checkVerificationProcessResult/{verificationId}")
    public ResponseEntity<Map> checkVerificationProcessResult(
            @PathVariable String verificationId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String url = gateway + "/mobbscan-agent/checkVerificationProcessResult/" + verificationId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        return response;
    }

    @GetMapping("/getImage/{verificationId}/{imageType}")
    public ResponseEntity<byte[]> getVerificationImage(
            @PathVariable String verificationId,
            @PathVariable ImageType imageType,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/mobbscan-agent/%s/image/%s", gateway, verificationId, imageType.name());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(response.getHeaders().getContentType());

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }

    @GetMapping("/getRecording/{verificationId}/{recordingType}")
    public ResponseEntity<byte[]> getRecording(
            @PathVariable String verificationId,
            @PathVariable RecordingType recordingType,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/mobbscan-agent/%s/recording/%s", gateway, verificationId, recordingType.name());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(response.getHeaders().getContentType());

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }

    @GetMapping("/getZip/{verificationId}")
    public ResponseEntity<byte[]> getZip(
            @PathVariable String verificationId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/api/zip/%s", gateway, verificationId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(response.getHeaders().getContentType());

        // Opcional: establecer nombre de archivo si el content-disposition viene en el response del servidor
        if (response.getHeaders().getContentDisposition() != null) {
            responseHeaders.setContentDisposition(response.getHeaders().getContentDisposition());
        }

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }

    @DeleteMapping("/deleteVerification/{verificationId}")
    public ResponseEntity<?> deleteVerification(
            @PathVariable String verificationId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/mobbscan-agent/api/verification/%s", gateway, verificationId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            headers.set("Authorization", authorizationHeader);
        } else if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No Bearer token provided and no stored access token available."));
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                String.class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(response.getHeaders().getContentType());

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }






    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptRequest {
        @JsonProperty("personalData")
        private Map<String, String> personalData;
        @JsonProperty("type")
        private String type;
        @JsonProperty("verificationId")
        private String verificationId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectRequest {
        @JsonProperty("rejectionComment")
        private String rejectionComment;
        @JsonProperty("type")
        private String type;
        @JsonProperty("verificationId")
        private String verificationId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendToReviewRequest {
        @JsonProperty("verificationId")
        private String verificationId;
    }
}
