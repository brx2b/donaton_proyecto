package com.usuarios.api_usuarios.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //ENDPOINTS EXPUESTOS SIN NECESIDAD DE TOKEN
                .csrf(AbstractHttpConfigurer::disable) // Necesario desactivar CSRF en APIs REST/JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/login").permitAll()         // Público para el BFF
                        .requestMatchers("/usuarios/nuevoUsuario").permitAll()  // Público si cualquiera se puede registrar
                        .requestMatchers(HttpMethod.GET, "/usuarios/**").permitAll() // Permitir consultas de usuario internas
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // sin sesiones
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}