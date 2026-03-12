package com.daw.cinemadaw.domain.cinema;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Room name is required") // Valida que el campo no esté vacío
    @Size(min = 2, max = 100, message = "Room name must be between 2 and 100 characters") // Valida que el campo tenga una longitud entre 2 y 100 caracteres
    @Column
    private String name;
    @Min(value = 1, message = "Capacity must be between 1 and 9999")
    @Max(value = 9999, message = "Capacity must be between 1 and 9999")
    @Column
    private int capacity;
    @ManyToOne // Indica que hi ha una relació de molts a un entre Room i Cinema, és a dir,
               // moltes sales poden pertànyer a un cinema.
    private Cinema cinema; // Relació amb Cinema, cada sala / sales pertany a un cinema
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true) // Indica que hi ha una relació de un a molts entre Room i Seat,
    // és a dir, una sala pot tenir moltes butaques. El mappedBy indica que la
    // relació està mapejada per l'atribut "room" a la classe Seat.
    private List<Seat> seats = new java.util.ArrayList<>(); // Relació amb Seat, una sala pot tenir moltes butaques

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    // Constructor sense arguments requerit per JPA/Hibernate
    public Room() {
    }



    public Room(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;
    }

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    @Override
    public String toString() {
        return "Room [id=" + id + ", name=" + name + ", capacity=" + capacity + "]";
    }

}
