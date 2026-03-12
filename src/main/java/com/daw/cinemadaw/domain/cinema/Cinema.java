package com.daw.cinemadaw.domain.cinema;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity // Indica que la clase sera persistent a la bd
public class Cinema {
    
    @Id // Indica que aquest camp és la clau primària de la taula a la base de dades
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Genera automàticament un valor únic per a cada nova instància de Cinema a la base de dades, utilitzant l'estratègia d'identitat (IDENTITY) que és comú en bases de dades relacionals.
    private long id; // Identificador (tipus long per a que no s'acabin els identificadors)

    @NotBlank(message = "Cinema name is required") // Valida que el camp no estigui buit
    @Size(min = 2, max = 100, message = "Cinema name must be between 2 and 100 characters") // Valida que el camp tingui una longitud entre 2 i 100 caràcters
    @Column // Indica que aquest camp serà una columna a la taula de la base de dades
    private String cinemaName; // Nom Cinema
    @NotBlank(message = "Address is required") // Valida que el camp no estigui buit
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters") // Valida que el camp tingui una longitud entre 5 i 200 caràcters
    @Column
    private String address; // Adreça
    @NotBlank(message = "City is required") // Valida que el camp no estigui buit
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters") // Valida que el camp tingui una longitud entre 2 i 100 caràcters
    @Column
    private String city; // Ciutat Cinema
    @NotNull(message = "Postal code is required") // Valida que el camp no sigui null
    @Min(value = 10000, message = "Postal code must be between 10000 and 99999")
    @Max(value = 99999, message = "Postal code must be between 10000 and 99999")
    @Column
    private Integer postalCode; // Codi Postal

    @OneToMany (mappedBy="cinema", cascade = CascadeType.ALL, orphanRemoval= true) // Indica que hi ha una relació de un a molts entre Cinema i Room, 
    // cascade = CascadeType.ALL significa que les operacions realitzades en Cinema (com guardar o eliminar) també s'aplicaran a les sales associades. 
    // és a dir, un cinema pot tenir moltes sales. El mappedBy indica que la relació està mapejada per l'atribut "cinema" a la classe Room.
    List<Room> rooms = new ArrayList<>(); // Relació amb Room, un cinema pot tenir moltes sales
    
    

    // Constructor
    
    public Cinema() { // Constructor obligatori per Spring Boot pugui utilitzar a la base de dades 
    }


    public Cinema(String cinemaName, String address, String city, Integer postalCode) {
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
    public Integer getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
