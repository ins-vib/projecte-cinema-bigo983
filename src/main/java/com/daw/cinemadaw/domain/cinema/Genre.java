package com.daw.cinemadaw.domain.cinema;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Entitat que representa un gènere cinematogràfic (Acció, Drama, Sci-Fi...)
// Es relaciona amb Movie mitjançant una relació N:M (una pel·lícula pot
// tenir més d'un gènere i un mateix gènere s'aplica a moltes pel·lícules)
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nom del gènere és obligatori")
    @Size(min = 2, max = 50, message = "El nom del gènere ha de tenir entre 2 i 50 caràcters")
    @Column(nullable = false, unique = true)
    private String name;

    // Relació inversa amb Movie (la part propietària és Movie.genres)
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    // Constructor buit obligatori per JPA
    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    // Necessari perquè Thymeleaf/Spring puguin comparar gèneres seleccionats
    // a partir de l'ID quan el formulari envia la llista d'IDs marcats
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return id != null && id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
