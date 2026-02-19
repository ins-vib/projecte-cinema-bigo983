package com.daw.cinemadaw.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.repository.CinemaRepository;

@Component
public class Proves implements CommandLineRunner { 
    // implements significa que la clase Proves implementa la interfaz CommandLineRunner, 
    // lo que permite ejecutar código al iniciar la aplicación.

    private CinemaRepository cinemaRepository; // Inyección de dependencia del repositorio de Cinema

    public Proves(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository; // Constructor para inyectar el repositorio de Cinema
    }

    @Override
    public void run(String... args) throws Exception {
        // Aquí puedes escribir código que se ejecutará al iniciar la aplicación
        // Por ejemplo, puedes insertar datos de prueba en la base de datos

        Cinema cinema1 = new Cinema("Ocine", "Gavarres, 45", "Tarragona", 43045);
        cinemaRepository.save(cinema1); // Guarda el objeto cinema1 en la base de datos
    }
    
}
