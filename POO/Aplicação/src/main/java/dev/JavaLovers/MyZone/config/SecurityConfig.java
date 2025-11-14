package dev.JavaLovers.MyZone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // opcional, caso precise de métodos HTTP específicos
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

    // 2. --- MÉTODO ATUALIZADO ---
    //    Configura quais rotas exigem quais grupos
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita o CSRF (necessário para POSTs do JS)
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // Permite acesso PÚBLICO ao teu front-end
                .requestMatchers("/", "/index.html", "/style.css", "/script.js").permitAll()
                
                // Permite acesso PÚBLICO aos endpoints de cadastro e login
                .requestMatchers("/api/usuarios/cadastro").permitAll()
                .requestMatchers("/api/usuarios/login").permitAll()

                // --- NOVAS REGRAS DE ACESSO ---
                // Exige que o utilizador tenha o grupo "USER" para aceder a qualquer
                // endpoint dentro de /api/midias/, /api/avaliacoes/ ou /api/tmdb/
                .requestMatchers("/api/midias/**").hasRole("USER")
                .requestMatchers("/api/avaliacoes/**").hasRole("USER")
                .requestMatchers("/api/tmdb/**").hasRole("USER")

                /* // Exemplo para o futuro (se implementares ADMIN):
                .requestMatchers("/api/admin/users").hasRole("ADMIN")
                */

                // Exige autenticação para qualquer outra rota (Fallback)
                .anyRequest().authenticated() 
            );
        
        return http.build();
    }
}