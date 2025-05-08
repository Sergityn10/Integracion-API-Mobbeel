package com.mobbScan_integration.MobbScan.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring. Es buena práctica incluirla.
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Aplicación stateless
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll() // Permitir acceso a endpoints de autenticación
                        .requestMatchers(HttpMethod.GET, "/user/info", "/api/foos/**").hasAuthority("SCOPE_read")
                        .requestMatchers(HttpMethod.POST, "/api/foos").hasAuthority("SCOPE_write")
                        .anyRequest().authenticated() // Todas las demás peticiones requieren autenticación
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())) // Configurar el decodificador de JWT
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Usa la misma clave secreta que para firmar
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtSecretString.getBytes(), "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                // .macAlgorithm(MacAlgorithm.HS512) // Opcional: especificar el algoritmo si es necesario
                .build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(withDefaults()) // Configura CORS, puedes personalizarlo con: .cors(cors -> cors.configurationSource(customCorsConfigurationSource()))
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers(HttpMethod.GET, "/user/info", "/api/foos/**").hasAuthority("SCOPE_read")
//                                .requestMatchers(HttpMethod.POST, "/api/foos").hasAuthority("SCOPE_write")
//                                .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
//                )
//                .oauth2ResourceServer(oauth2ResourceServer ->
//                        oauth2ResourceServer
//                                .jwt(withDefaults()) // Configura la validación de JWT, puedes personalizarlo con: .jwt(jwt -> jwt.jwtAuthenticationConverter(customConverter()))
//                );
//        return http.build();
//    }
}
