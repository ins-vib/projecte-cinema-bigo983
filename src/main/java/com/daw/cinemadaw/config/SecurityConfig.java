package com.daw.cinemadaw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

         http
        // Desactiva CSRF (necessari per H2)
        .csrf(csrf -> csrf.disable())

        // Permet carregar la consola H2 en un iframe
        .headers(headers -> headers
            .frameOptions(frame -> frame.disable())
        )

        // Configuració d'autoritzacions
        .authorizeHttpRequests(auth -> auth

            // Accés públic
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/", "/home", "/login", "/css/**").permitAll()

            // Rutes de panells (entren ADMIN i CLIENT)
            .requestMatchers("/admin", "/admin/**").hasAnyRole("ADMIN", "CLIENT")
            .requestMatchers("/client", "/client/**").hasAnyRole("CLIENT", "ADMIN")

            // Rutes de controladors (entren ADMIN i CLIENT)
            .requestMatchers(
                "/cinemes",
                "/cinema/**",
                "/movies/**",
                "/room/**",
                "/seat/**",
                "/seats/**",
                "/screenings/**"
            ).hasAnyRole("ADMIN", "CLIENT")

            // Qualsevol altra petició necessita autenticació
            .anyRequest().authenticated()
        )

        // Configuració del formulari de login
        .formLogin(form -> form
            .loginPage("/login") // pàgina personalitzada de login
            .successHandler(new CustomLoginSuccessHandler()) // redirecció segons rol
            .permitAll()
        )

        // Configuració del logout
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

        return http.build();
    }

    // Bean d'encriptació flexible ({bcrypt}, {noop}, etc.)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}