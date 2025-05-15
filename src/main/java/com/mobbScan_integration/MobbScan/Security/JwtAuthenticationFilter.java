package com.mobbScan_integration.MobbScan.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String header = request.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//            if (jwtTokenProvider.validateToken(token)) {
//                String username = jwtTokenProvider.getUsernameFromJWT(token);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        username, null, List.of() // aquí puedes inyectar roles si los usas
//                );
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    String path = request.getRequestURI();
    if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
        filterChain.doFilter(request, response);
        return;
    }
    if (request.getServletPath().startsWith("/api/auth")) {
        filterChain.doFilter(request, response);
        return;
    }

    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromJWT(token);

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);


        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            return; // Detiene aquí la cadena de filtros
        }
    }

    filterChain.doFilter(request, response);
}
}
