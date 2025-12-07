package com.polimarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para la aplicación
 * Define las reglas de acceso a los endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad
     * Permite acceso público a ciertos endpoints relacionados con empleados
     *
     * @param http configurador de HttpSecurity
     * @return SecurityFilterChain configurada
     * @throws Exception si hay errores en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Permitir acceso público a los endpoints de RRHH (Empleados)
                .requestMatchers("/api/rrhh/**").permitAll()
                // Permitir acceso al endpoint de salud
                .requestMatchers("/api/health").permitAll()
                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                // Todos los demás requieren autenticación
                .anyRequest().authenticated()
        )
        .csrf().disable()
        .headers().frameOptions().disable();

        return http.build();
    }

    /**
     * Bean para codificar contraseñas
     * Utiliza BCrypt para mayor seguridad
     *
     * @return PasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
