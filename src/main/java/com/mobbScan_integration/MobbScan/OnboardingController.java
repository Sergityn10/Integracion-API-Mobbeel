package com.mobbScan_integration.MobbScan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mobbScan_integration.MobbScan.DTO.AcceptRequest;
import com.mobbScan_integration.MobbScan.DTO.PendingRequest;
import com.mobbScan_integration.MobbScan.DTO.RejectRequest;
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
import java.util.List;
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
public ResponseEntity<Map> doOnboarding(@RequestBody OnboardingRequest onboardingRequest) {
    System.out.println("ACCESS TOKEN: " + accessToken);

    // Intentar operación con el token actual
    ResponseEntity<Map> response = tryOnboarding(onboardingRequest, this.accessToken);

    // Si fue 401 Unauthorized, entonces el token expiró o es inválido → Reintenta
    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        System.out.println("Token expirado o inválido. Reautenticando...");
        boolean refreshed = refreshAccessToken();
        if (refreshed) {
            System.out.println("Nuevo token obtenido. Reintentando onboarding...");
            return tryOnboarding(onboardingRequest, this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No se pudo autenticar con MobbScan."));
        }
    }

    return response;
}


    @GetMapping("/getVerificationProcessData/{verificationId}")
    public ResponseEntity<Map> getVerificationProcessData(
            @PathVariable String verificationId
    ) {
        String url = gateway + "/mobbscan-agent/getVerificationProcessData/" + verificationId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (this.accessToken != null) {
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

        if (this.accessToken != null) {
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

        if (this.accessToken != null) {
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
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/mobbscan-agent/%s/recording/%s", gateway, verificationId, recordingType.name());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (this.accessToken != null) {
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

        if (this.accessToken != null) {
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

    @PostMapping("/accept")
    public ResponseEntity<Map> sendToAccept(
            @RequestBody AcceptRequest acceptRequest
    ) {
        String url = gateway + "/mobbscan-agent/accept";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }

        HttpEntity<AcceptRequest> request = new HttpEntity<>(acceptRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return response;
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<Map> sendToReject(
            @RequestBody RejectRequest rejectRequest
    ) {
        String url = gateway + "/mobbscan-agent/reject";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

       if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }

        HttpEntity<RejectRequest> request = new HttpEntity<>(rejectRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return response;
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }



    @DeleteMapping("/{verificationId}")
    public ResponseEntity<?> deleteVerification(
            @PathVariable String verificationId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader
    ) {
        String url = String.format("%s/mobbscan-agent/api/verification/%s", gateway, verificationId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", acceptHeader);

        if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken); 
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No Bearer token provided and no stored access token available."));
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(response.getHeaders().getContentType());

            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

        }catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }


    }

    @PostMapping("/pending")
    public ResponseEntity<Map> sendToPending(
            @RequestBody PendingRequest pendingRequest
    ) {
        String url = gateway + "/mobbscan-agent/pending";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (this.accessToken != null) {
            headers.setBearerAuth(this.accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "No Bearer token provided and no stored access token available.")
            );
        }

        HttpEntity<PendingRequest> request = new HttpEntity<>(pendingRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return response;
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }

}
