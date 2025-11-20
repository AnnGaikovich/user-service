package org.example.userservice.auth.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InternalApiKeyFilter.class);

    @Value("${internal.api-key:internal-auth-key-12345}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Применяем фильтр только к внутренним эндпоинтам
        if (requestUri.startsWith("/api/v1/internal/")) {
            String apiKey = request.getHeader("X-API-Key");

            if (apiKey == null || !apiKey.equals(validApiKey)) {
                log.warn("Invalid or missing API key for internal endpoint: {}", requestUri);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid API key");
                return;
            }

            // Устанавливаем аутентификацию с ролью SERVICE
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "internal-service",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Internal API request authenticated for: {}", requestUri);
        }

        filterChain.doFilter(request, response);
    }
}