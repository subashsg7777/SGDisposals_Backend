package com.subash.SGDisposals.security;

import com.subash.SGDisposals.RateLimitType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitTypeDecider rateLimitTypeDecider;
    private final Environment env;
    private final RateLimitService rateLimitService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RateLimitType type = rateLimitTypeDecider.findType(request);

        if(type == RateLimitType.NONE){
            filterChain.doFilter(request,response);
            return;
        }

        String ipAddress = extractClientIp(request);
        String key = buildKey(type,ipAddress);

        int limit = getLimit(type);
        int window = getWindow(type);

        if (!rateLimitService.isAllowed(key, limit, window)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request,response);
        return;
    }

    private int getWindow(RateLimitType type) {
        int window = Integer.parseInt(env.getProperty("rate-limit."+type.name().toLowerCase()+".window-seconds"));
        logger.info("The Window Seconds for request is : "+ window);
        return window;
    }

    private int getLimit(RateLimitType type) {
        int limit =  Integer.parseInt(env.getProperty("rate-limit."+type.name().toLowerCase()+".limit"));
        logger.info("The Limit for req is "+ limit);
        return limit;
    }

    private String buildKey(RateLimitType type, String ipAddress) {
        return "rate:" + type.name().toLowerCase() + ":ip:" + ipAddress;
    }

    private String extractClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }
}
