package com.daw.cinemadaw.domain.cinema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)

    private long id;

    @NotBlank(message = "El títol és obligatori")
    @Size(min = 2, max = 100, message = "El títol ha de tenir entre 2 i 100 caràcters")
    @Column
    private String title;
    @NotNull(message = "La durada és obligatòria")
    @Min(value = 1, message = "La durada ha d'estar entre 1 i 9999 minuts")
    @Max(value = 9999, message = "La durada ha d'estar entre 1 i 9999 minuts")
    @Column
    private Integer duration;

    // Relació N:M amb Genre. Una pel·lícula pot tenir varis gèneres
    // (Acció + Aventura + Thriller, p. ex.) i un mateix gènere es repeteix
    // a moltes pel·lícules. Es genera la taula intermèdia "movie_genres".
    @NotEmpty(message = "Has de seleccionar com a mínim un gènere")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @NotBlank(message = "La descripció és obligatòria")
    @Size(min = 10, max = 1000, message = "La descripció ha de tenir entre 10 i 1000 caràcters")
    @Column(length = 1000)
    private String description;
    @NotNull(message = "La data de llançament és obligatòria")
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screening> screenings = new ArrayList<>();

    public Movie() {
    }

    public Movie(String title, int duration, String description, LocalDate releaseDate) {
        this.title = title;
        this.duration = duration;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
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

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", duration=" + duration + ", genres=" + genres
                + ", description=" + description + ", releaseDate=" + releaseDate + "]";
    }
}
