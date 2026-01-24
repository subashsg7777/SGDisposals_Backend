package com.subash.SGDisposals.security;

import com.subash.SGDisposals.RateLimitType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@Component
public class RateLimitTypeDecider {

    public RateLimitType findType(HttpServletRequest request){
        String uri = request.getRequestURI();

        if(uri.startsWith("/api/v2/user/login") || uri.startsWith("/api/v2/user/add-user")){
            return RateLimitType.HIGH;
        }

        if(uri.startsWith("/api/v2/products/search")){
            return RateLimitType.MEDIUM;
        }

        return RateLimitType.NONE;
    }
}
