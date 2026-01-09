package com.subash.SGDisposals.security;

import com.subash.SGDisposals.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

//        log.info(
//                "JwtAuthFilter - Authorization header: {} Method: {} Path: {}",
//                authHeader, request.getMethod(), request.getRequestURI()
//        );

        // 1️⃣ No header OR wrong prefix → skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("TOKEN IS NOT PRESENT IN REQUEST");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 2️⃣ Token sanity check
        if (token.isBlank() || token.equals("null") || token.equals("undefined")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3️⃣ JWT structural check (xxx.yyy.zzz)
        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                log.error("INVALID TOKEN");
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token).toString();

            log.info("Auth Header: {}", authHeader);


            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.warn("JwtAuthFilter - invalid token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}