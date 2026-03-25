package com.daw.cinemadaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daw.cinemadaw.domain.cinema.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}