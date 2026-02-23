package com.daw.cinemadaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Cinema;

@Repository// Indica que aquesta interfície és un repositori de Spring, 
// el que permet a Spring gestionar-la com a component de persistència de dades.
 
public interface CinemaRepository extends JpaRepository<Cinema, Long> { 
// JpaRepository és una interfície de Spring Data JPA que proporciona mètodes per a realitzar operacions CRUD 
// (Create, Read, Update, Delete) i altres operacions relacionades amb la gestió d'entitats a la base de dades.
//  En aquest cas, estem indicant que el repositori gestionarà entitats de tipus Cinema i que la clau primària d'aquestes 
// entitats és de tipus Long.
 
List<Cinema> findByCity(String city); 


}

