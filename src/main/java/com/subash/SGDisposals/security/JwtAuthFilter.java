package com.subash.SGDisposals.security;

import com.subash.SGDisposals.exception.UnauthorizedRequestException;
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
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Public endpoints
        if (uri.startsWith("/api/v2/user/login")
                || uri.startsWith("/api/v2/user/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // No header → let Spring Security handle it (403)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty() || token.equalsIgnoreCase("null")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Empty JWT token");
            return;
        }

        // JWT format sanity check
        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
            return;
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
                return;
            }

            String email = jwtUtil.extractEmail(token);
            String role = String.valueOf(jwtUtil.extractRole(token));

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
            log.error("JWT authentication failed", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
