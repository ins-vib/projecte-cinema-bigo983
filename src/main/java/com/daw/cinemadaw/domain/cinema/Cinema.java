package com.daw.cinemadaw.domain.cinema;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity // Indica que la clase sera persistent a la bd
public class Cinema {
    
    @Id // Indica que aquest camp és la clau primària de la taula a la base de dades
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Genera automàticament un valor únic per a cada nova instància de Cinema a la base de dades, utilitzant l'estratègia d'identitat (IDENTITY) que és comú en bases de dades relacionals.
    private long id; // Identificador (tipus long per a que no s'acabin els identificadors)

    @Column // Indica que aquest camp serà una columna a la taula de la base de dades
    private String cinemaName; // Nom Cinema
    @Column
    private String address; // Adreça
    @Column
    private String city; // Ciutat Cinema
    @Column
    private int postalCode; // Codi Postal

    @OneToMany (mappedBy="cinema") // Indica que hi ha una relació de un a molts entre Cinema i Room, 
    // és a dir, un cinema pot tenir moltes sales. El mappedBy indica que la relació està mapejada per l'atribut "cinema" a la classe Room.
    List<Room> rooms = new ArrayList<>(); // Relació amb Room, un cinema pot tenir moltes sales
    


    // Constructor
    
    public Cinema() { // Constructor obligatori per Spring Boot pugui utilitzar a la base de dades 
    }


    public Cinema(String cinemaName, String address, String city, int postalCode) {
        this.cinemaName = cinemaName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
    }

    // Getters , Setters

    
    public String getCinemaName() {
        return cinemaName;
    }
    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public long getId() {
        return id;
    }


    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "Cinema [id=" + id + ", cinemaName=" + cinemaName + ", address=" + address + ", city=" + city
                + ", postalCode=" + postalCode + "]";
    }            



    
}
