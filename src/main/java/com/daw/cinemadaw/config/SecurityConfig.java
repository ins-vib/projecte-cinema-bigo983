package com.daw.cinemadaw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/cookies/**", "/session", "/session/**").permitAll()

            // Panells separats per rol
            .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
            .requestMatchers("/client", "/client/**").hasRole("CLIENT")

            // Gestió: només ADMIN
            .requestMatchers(
                "/cinemes/**",
                "/cinema/**",
                "/room/**",
                "/seat/**",
                "/seats/**",
                "/movies/create-movies",
                "/movies/create",
                "/movies/update/**",
                "/movies/update",
                "/movies/delete/**",
                "/screenings/new",
                "/screenings/new/**",
                "/screenings/edit/**",
                "/screenings/update",
                "/screenings/delete/**",
                "/admin/returns",
                "/admin/returns/**"
            ).hasRole("ADMIN")

            // Reserva de seients: només CLIENT pot confirmar-la (POST)
            .requestMatchers(HttpMethod.POST, "/screenings/reserve").hasRole("CLIENT")

            // Consulta/reserva: ADMIN i CLIENT
            .requestMatchers(
                "/movies/movies",
                "/movies/detail/**",
                "/movies/projections/**",
                "/screenings/reserve/**",
                "/screenings/*",
                "/cart",
                "/cart/**",
                "/tickets",
                "/tickets/*/return"
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