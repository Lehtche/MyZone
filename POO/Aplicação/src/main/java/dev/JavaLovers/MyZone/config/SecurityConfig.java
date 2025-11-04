package dev.JavaLovers.MyZone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Diz ao Spring como criptografar (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Configura quais rotas são públicas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita o CSRF (necessário para POSTs do JS)
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // Permite acesso PÚBLICO ao seu front-end (/, index.html, etc)
                .requestMatchers("/", "/index.html", "/style.css", "/script.js").permitAll()
                // Permite acesso PÚBLICO aos endpoints de cadastro e login
                .requestMatchers("/api/usuarios/cadastro", "/api/usuarios/login").permitAll()
                // Exige autenticação para qualquer outra rota
                .anyRequest().authenticated() 
            );
        
        return http.build();
    }
}