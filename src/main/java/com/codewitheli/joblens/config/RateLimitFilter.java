package com.codewitheli.joblens.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A lightweight per-client token-bucket rate limiter for the write/compute
 * endpoints, so the public demo can't be hammered. Keyed by the X-Client-Id
 * header (the UI sends a random browser id) or the remote address.
 */
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String[] LIMITED_PREFIXES = {
            "/api/analyze", "/api/applications", "/api/import", "/api/profile/extract"
    };
    private static final int MAX_BUCKETS = 10_000;

    private final RateLimitProperties props;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties props) {
        this.props = props;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.isEnabled() || !"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        for (String p : LIMITED_PREFIXES) {
            if (path.startsWith(p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = clientKey(request);
        if (buckets.size() > MAX_BUCKETS) {
            buckets.clear(); // crude safety valve for an unbounded key space
        }
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(props.getCapacity()));
        double refillPerSec = props.getRefillPerMinute() / 60.0;

        if (bucket.tryConsume(props.getCapacity(), refillPerSec)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Too many requests — please slow down and try again shortly.\"}");
        }
    }

    private String clientKey(HttpServletRequest request) {
        String id = request.getHeader("X-Client-Id");
        if (id != null && !id.isBlank()) {
            return "cid:" + id.trim();
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        return "ip:" + ip;
    }

    /** A thread-safe token bucket. */
    private static final class Bucket {
        private double tokens;
        private long lastRefillNanos;

        Bucket(int initial) {
            this.tokens = initial;
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized boolean tryConsume(double capacity, double refillPerSec) {
            long now = System.nanoTime();
            double elapsedSec = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsedSec * refillPerSec);
            lastRefillNanos = now;
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }
}
