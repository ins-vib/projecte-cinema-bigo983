package com.daw.cinemadaw.domain.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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



    
}
