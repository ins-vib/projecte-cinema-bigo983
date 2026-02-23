package com.daw.cinemadaw.config;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.SeatRepository;

import jakarta.transaction.Transactional;

@Component
public class Proves implements CommandLineRunner {
    // implements significa que la clase Proves implementa la interfaz
    // CommandLineRunner,
    // lo que permite ejecutar código al iniciar la aplicación.

    private final CinemaRepository cinemaRepository; // Inyección de dependencia del repositorio de Cinema
    private final RoomRepository roomRepository; // Inyección de dependencia del repositorio de Room
    private final SeatRepository seatRepository; // Inyección de dependencia del repositorio de Seat

    public Proves(CinemaRepository cinemaRepository, RoomRepository roomRepository, SeatRepository seatRepository) {
        this.cinemaRepository = cinemaRepository; // Constructor para inyectar el repositorio de Cinema
        this.roomRepository = roomRepository; // Constructor para inyectar el repositorio de Room
        this.seatRepository = seatRepository; // Constructor para inyectar el repositorio de Seat
    }
    @Transactional // Indica que el método run se ejecutará dentro de una transacción, lo que es útil para asegurar la integridad de los datos al realizar operaciones de base de datos. Si alguna operación falla, la transacción se revertirá automáticamente.
    @Override
    public void run(String... args) throws Exception {
        // Aquí puedes escribir código que se ejecutará al iniciar la aplicación
        // Por ejemplo, puedes insertar datos de prueba en la base de datos
        // -------------------------------Cinema-------------------------------
        Cinema cinema1 = new Cinema("Ocine", "Gavarres, 45", "Tarragona", 43045);
        cinemaRepository.save(cinema1); // Guarda el objeto cinema1 en la base de datos


        Room room1 = new Room(100, "Sala 1"); // Crea un nuevo objeto Room con capacidad 100 y nombre "Sala 1"
        room1.setCinema(cinema1); // Establece la relación entre la sala y el cine
        roomRepository.save(room1); // Guarda el objeto room1 en la base de datos

        Room room2 = new Room(150, "Sala 2");
        room2.setCinema(cinema1);
        roomRepository.save(room2);

        Room room3 = new Room(200, "Sala 3");
        room3.setCinema(cinema1);
        roomRepository.save(room3);


        System.out.println(room3.getCinema().getCinemaName()); // Imprime el nombre del cine al que pertenece la sala room3


        Optional<Cinema> optionalCinema = cinemaRepository.findById(4L);
        if (optionalCinema.isPresent()) {
        Cinema cinema = optionalCinema.get(); // Si el cinema existeix es recupera el objeto Cinema del Optional
        List<Room> rooms = cinema.getRooms(); // Recupera la lista de salas asociada al cine
        for (Room room : rooms){
            System.out.println(room); // Imprime cada sala en la consola (esto requiere que la clase Room tenga un método toString() adecuado)
        }
        } else {
        System.out.println("No s'ha trobat el cinema"); // Si no se encontró un Cinema con el ID especificado,
                }

        // Seat seat1 = new Seat(1, 0, 0, "A");
        // seat1.setTypeSeat(SeatType.PREMIUM);
        // seatRepository.save(seat1);
        List<Room> allRooms = roomRepository.findAll();
        for(int i = 0; i < allRooms.size(); i++) {
            int x = 0;
            int y = 0;
            Room currentRoom = allRooms.get(i);
            int capacitatPerFila = currentRoom.getCapacity();
            int seatsForRow = 10;
            String seatRow;
            for (int j = 0; j < capacitatPerFila; j++) {
                int columna = j % seatsForRow; // Calcula la columna de la butaca basándose en el número de butacas por fila
                seatRow = String.valueOf((char)('A' + (j / seatsForRow))); // Calcula la fila de la butaca (A, B, C, etc.) basándose en el número de butacas por fila
                Seat seat1 = new Seat(j, x, y, seatRow);
                seatRepository.save(seat1);
                x += 5;
                if(columna == 9 && j != 0) {// Si se ha alcanzado el número de butacas por fila, se incrementa la coordenada y para la siguiente fila
                    y += 5;
                } 
                if (columna == 9) { // Si se ha alcanzado el número de butacas por fila, se reinicia la coordenada x para la siguiente fila
                    x = 0;
                }
            }
        }
        // List<Cinema> llista = cinemaRepository.findAll(); // Recupera todos los objetos Cinema de la base de datos
        // // El findAll() es un metodo que en el mon real es sol limitar a x registres per
        // // evitar sobrecargues de memoria
        // for (Cinema cinema : llista) {
        //     System.out.println(cinema); // Imprime cada objeto Cinema en la consola (esto requiere que la clase Cinema
        //                                 // tenga un método toString() adecuado)
        // }

        // // S'ha de posar un Optional per evitar que el programa peti si no existeix un
        // // Cinema amb l'ID especificat
        // Optional<Cinema> optionalCinema = cinemaRepository.findById(4L); // Busca un Cinema por su ID (en este caso, el
        //                                                                  // ID 1) 1L indica que el ID es de tipo long
        // if (optionalCinema.isPresent()) { // Verifica si el Optional contiene un valor (es decir, si se encontró un
        //                                   // Cinema con el ID especificado)
        //     Cinema cinema = optionalCinema.get(); // Si el cinema existeix es recupera el objeto Cinema del Optional
        //     System.out.println(cinema); // Imprime el objeto Cinema encontrado en la consola
        //     cinema.setCity("Reus");
        //     cinemaRepository.save(cinema);
        // } else {
        //     System.out.println("No s'ha trobat el cinema"); // Si no se encontró un Cinema con el ID especificado,
        //                                                     // imprime un mensaje indicando que no se encontró
        // }

        // List<Cinema> llista2 = cinemaRepository.findByCity("Tarragona");
        // for (Cinema cinema : llista2) {
        //     System.out.println(cinema);
        // }

        // cinemaRepository.delete(llista2.get(0));

        // llista = cinemaRepository.findAll();
        // for (Cinema cinema : llista) {
        //     System.out.println(cinema);
        // }

        // // -------------------------------Proves-------------------------------

    }

}
