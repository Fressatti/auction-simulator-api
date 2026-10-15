package com.example.auction.adapter.in.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final var requestId = UUID.randomUUID().toString();
        final var previousRequestId = MDC.get("requestId");
        final long started = System.nanoTime();
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-ID", requestId);
        try {
            log.info("HTTP request started requestId={} method={} path={}",
                    requestId, request.getMethod(), request.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            log.info("HTTP request completed requestId={} method={} path={} status={} durationMs={}",
                    requestId, request.getMethod(), request.getRequestURI(), response.getStatus(),
                    (System.nanoTime() - started) / 1_000_000);
            if (previousRequestId == null) MDC.remove("requestId");
            else MDC.put("requestId", previousRequestId);
        }
    }
}
