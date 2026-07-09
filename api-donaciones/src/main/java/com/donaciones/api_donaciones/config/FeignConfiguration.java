package com.donaciones.api_donaciones.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfiguration {

    private static final ThreadLocal<String> authorizationToken = new ThreadLocal<>();

    public static void setAuthorizationToken(String token) {
        authorizationToken.set(token);
    }

    public static void clearAuthorizationToken() {
        authorizationToken.remove();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                System.out.println("[FeignInterceptor] Interceptor activado.");

                // Primero intenta usar ThreadLocal (pasado explícitamente desde el controller)
                String token = authorizationToken.get();
                if (token != null) {
                    System.out.println("[FeignInterceptor] Token encontrado en ThreadLocal: " + token.substring(0, Math.min(token.length(), 20)) + "...");
                    requestTemplate.header("Authorization", token);
                    return;
                }

                try {
                    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
                    if (attributes instanceof ServletRequestAttributes) {
                        ServletRequestAttributes servletAttributes = (ServletRequestAttributes) attributes;
                        HttpServletRequest request = servletAttributes.getRequest();
                        String authHeader = request.getHeader("Authorization");
                        if (authHeader != null) {
                            System.out.println("[FeignInterceptor] Token encontrado en RequestContextHolder: " + authHeader.substring(0, Math.min(authHeader.length(), 20)) + "...");
                            requestTemplate.header("Authorization", authHeader);
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[FeignInterceptor] No se pudo acceder a RequestContextHolder: " + e.getMessage());
                }

                System.out.println("[FeignInterceptor] ALERTA: No se encontró token para propagar.");
            }
        };
    }
}
