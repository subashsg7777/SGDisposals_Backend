package com.subash.SGDisposals.security;

public interface IRateLimitService {
    boolean isAllowed(String key, int limit, int windowSeconds);
}
