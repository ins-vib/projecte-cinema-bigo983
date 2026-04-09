package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;

@Controller
public class ScreeningController {

    private ScreeningRepository screeningRepository;
    private RoomRepository roomRepository;
    private CinemaRepository cinemaRepository;

    public ScreeningController(ScreeningRepository screeningRepository, RoomRepository roomRepository, CinemaRepository cinemaRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
    }

    @GetMapping("/movies/projections/{id}")
    public String projections(@PathVariable Long id, Model model) {
        List<Screening> screenings = screeningRepository.findByMovieId(id);
        model.addAttribute("screenings", screenings);
        model.addAttribute("movieId", id);
        return "projections/Screening";
    }

    @GetMapping("/screenings/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Screening screening = screeningRepository.findById(id).orElse(null);

        model.addAttribute("screening", screening);
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "projections/ScreeningEdit";
    }

    @PostMapping("/screenings/update")
    public String update(Screening screening) {
        screeningRepository.save(screening);
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/new")
    public String mostrarFormNew(@RequestParam Long movieId, Model model) {
        Screening screening = new Screening();
        Movie movie = new Movie();
        movie.setId(movieId);
        screening.setMovie(movie);
        model.addAttribute("screening", screening);
        model.addAttribute("rooms", roomRepository.findAll());
        return "projections/ScreeningNew"; 
    }

    @PostMapping("/screenings/new")
    public String create(Screening screening) {
        screeningRepository.save(screening);
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/delete/{id}")
    public String delete(@PathVariable Long id) {
        screeningRepository.deleteById(id);
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/reserve/{id}")
    public String reserve(@PathVariable Long id, Model model) {
        Screening screening = screeningRepository.findById(id).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "redirect:/movies/movies";
        }

        model.addAttribute("screening", screening);
        Long roomId = screening.getRoom().getId();
        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isPresent()) {
            Room room = optional.get();
            List<Seat> seats = room.getSeats();
            model.addAttribute("seats", seats);
            model.addAttribute("roomId", roomId);
            return "projections/ScreeningReserve";
        }

        return "redirect:/movies/movies";
    }

    @PostMapping("/screenings/reserve")
    public String reserveSeats(@RequestParam Long screeningId, @RequestParam(required = false) List<Long> seatIds) {
        Optional<Screening> optionalScreening = screeningRepository.findById(screeningId);
        if (optionalScreening.isPresent()) {
            Screening screening = optionalScreening.get();
            Room room = screening.getRoom();
            List<Seat> seats = room.getSeats();

            if (seatIds != null && !seatIds.isEmpty()) {
                for (Seat seat : seats) {
                    if (seatIds.contains(seat.getId())) {
                        seat.setState(false);
                    }
                }
            }

            roomRepository.save(room);
        }

        return "redirect:/movies/movies";        

    }
}
