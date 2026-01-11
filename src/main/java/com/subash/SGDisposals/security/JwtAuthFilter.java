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
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();   // e.g. "/api/v2/user/login"

        if (uri.equals("/api/v2/user/login") || uri.equals("/api/v2/user/signup")) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedRequestException("User Header is Empty or Corrupted");
        }

        String token = authHeader.substring(7);

        if (token.isBlank() || token.equals("null") || token.equals("undefined")) {
            throw new UnauthorizedRequestException("Token is Empty or Corrupted !");
        }

        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            throw new UnauthorizedRequestException("Invalid Token Format");
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                throw new UnauthorizedRequestException("Invalid Or Expired Token");
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token).toString();
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