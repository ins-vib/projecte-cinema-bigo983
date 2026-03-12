package com.daw.cinemadaw.domain.cinema;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Movie {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)

    private long id; // Identificador 

    @NotBlank(message = "Movie title is required") // Valida que el campo no esté vacío
    @Size(min = 2, max = 100, message = "Movie title must be between 2 and 100 characters") // Valida que el campo tenga una longitud entre 2 y 100 caracteres
    @Column
    private String title;
    @Min(value = 1, message = "Duration must be between 1 and 9999 minutes")
    @Max(value = 9999, message = "Duration must be between 1 and 9999 minutes")
    @Column
    private int duration; // Durada en minuts
    @NotBlank(message = "Genre is required") // Valida que el campo no esté vacío
    @Size(min = 2, max = 100, message = "Genre must be between 2 and 100 characters") // Valida que el campo tenga una longitud entre 2 y 100 caracteres
    @Column
    private String genre; // Gènere de la pel·lícula
    @NotBlank(message = "Description is required") // Valida que el campo no esté vacío
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters") // Valida que el campo tenga una longitud entre 10 y 1000 caracteres
    @Column
    private String description; // Descripció de la pel·lícula
    @NotNull(message = "Release date is required")
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate; // Data de llançament de la pel·lícula

    public Movie() {
    }

    public Movie(String title, int duration, String genre, String description, LocalDate releaseDate) {
        this.title = title;
        this.duration = duration;
        this.genre = genre;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", duration=" + duration + ", genre=" + genre
                + ", description=" + description + ", releaseDate=" + releaseDate + "]";
    }
}
