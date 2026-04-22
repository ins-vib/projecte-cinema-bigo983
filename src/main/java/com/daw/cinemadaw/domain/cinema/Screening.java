package com.daw.cinemadaw.domain.cinema;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
public class Screening {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "La data i hora són obligatòries")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    @NotNull(message = "El preu és obligatori")
    @DecimalMin(value = "0.50", message = "El preu ha d'estar entre 0.50 i 100.00 €")
    @DecimalMax(value = "100.00", message = "El preu ha d'estar entre 0.50 i 100.00 €")
    private Double price;

    @NotNull(message = "La pel·lícula és obligatòria")
    @ManyToOne
    private Movie movie;

    @NotNull(message = "La sala és obligatòria")
    @ManyToOne
    private Room room;

    public Screening() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
