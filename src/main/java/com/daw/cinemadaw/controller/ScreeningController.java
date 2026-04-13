package com.daw.cinemadaw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.dto.SeatsListDTO;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ScreeningController {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;

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

        SeatsListDTO seatsListDTO = new SeatsListDTO();
        seatsListDTO.setScreeningId(screening.getId());
        model.addAttribute("seatsListDTO", seatsListDTO);
        model.addAttribute("seats", screening.getRoom().getSeats());
        model.addAttribute("screening", screening);

        return "projections/ScreeningReserve";
    }

    @PostMapping("/screenings/reserve")
    public String reserveSeats(@ModelAttribute("seatsListDTO") SeatsListDTO selectedSeats, HttpSession session) {
        if (selectedSeats.getScreeningId() == null) {
            return "redirect:/movies/movies";
        }

        List<Long> selectedSeatIds = selectedSeats.getSeatIds();
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            return "redirect:/screenings/reserve/" + selectedSeats.getScreeningId();
        }

        Object currentCartObject = session.getAttribute("cart");
        if (currentCartObject instanceof SeatsListDTO currentCart
                && currentCart.getScreeningId() != null
                && currentCart.getSeatIds() != null) {
            Screening oldScreening = screeningRepository.findById(currentCart.getScreeningId()).orElse(null);
            if (oldScreening != null && oldScreening.getRoom() != null) {
                for (Seat seat : oldScreening.getRoom().getSeats()) {
                    if (currentCart.getSeatIds().contains(seat.getId())) {
                        seat.setState(true);
                    }
                }
                roomRepository.save(oldScreening.getRoom());
            }
        }

        Screening screening = screeningRepository.findById(selectedSeats.getScreeningId()).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "redirect:/movies/movies";
        }

        for (Seat seat : screening.getRoom().getSeats()) {
            if (selectedSeatIds.contains(seat.getId())) {
                seat.setState(false);
            }
        }
        roomRepository.save(screening.getRoom());

        SeatsListDTO cart = new SeatsListDTO();
        cart.setScreeningId(selectedSeats.getScreeningId());
        cart.setSeatIds(new ArrayList<>(selectedSeatIds));
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }
}
    