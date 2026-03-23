package com.daw.cinemadaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.cinemadaw.domain.cinema.User;

// Repositori JPA per gestionar usuaris
public interface UserRepository extends JpaRepository<User, Long> {

    // Cerca un usuari pel seu username
    Optional<User> findByUsername(String username);
}