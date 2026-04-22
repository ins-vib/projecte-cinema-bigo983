package com.daw.cinemadaw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.dto.SeatsListDTO;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.MovieRepository;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ScreeningController {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;

    public ScreeningController(ScreeningRepository screeningRepository, RoomRepository roomRepository,
                               CinemaRepository cinemaRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies/projections/{id}")
    public String projections(@PathVariable Long id, Model model) {
        List<Screening> screenings = screeningRepository.findByMovieId(id);
        Optional<Movie> movie = movieRepository.findById(id);
        model.addAttribute("screenings", screenings);
        model.addAttribute("movieId", id);
        movie.ifPresent(m -> model.addAttribute("movie", m));
        return "projections/Screening";
    }

    @GetMapping("/screenings/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("screening", optional.get());
            return "projections/ScreeningDetail";
        }
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/movies/movies";
        }
        model.addAttribute("screening", optional.get());
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "projections/ScreeningEdit";
    }

    @PostMapping("/screenings/update")
    public String update(@Valid @ModelAttribute("screening") Screening screening,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepository.findAll());
            model.addAttribute("cinemas", cinemaRepository.findAll());
            return "projections/ScreeningEdit";
        }
        screeningRepository.save(screening);
        Long movieId = screening.getMovie() != null ? screening.getMovie().getId() : null;
        if (movieId != null) {
            return "redirect:/movies/projections/" + movieId;
        }
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
    public String create(@Valid @ModelAttribute("screening") Screening screening,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepository.findAll());
            return "projections/ScreeningNew";
        }
        screeningRepository.save(screening);
        Long movieId = screening.getMovie() != null ? screening.getMovie().getId() : null;
        if (movieId != null) {
            return "redirect:/movies/projections/" + movieId;
        }
        return "redirect:/movies/movies";
    }

    @PostMapping("/screenings/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La projecció no s'ha trobat.");
            return "redirect:/movies/movies";
        }
        Long movieId = optional.get().getMovie() != null ? optional.get().getMovie().getId() : null;
        try {
            screeningRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Projecció eliminada correctament.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No s'ha pogut eliminar la projecció. Pot tenir reserves associades.");
        }
        if (movieId != null) {
            return "redirect:/movies/projections/" + movieId;
        }
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
    public String reserveSeats(@ModelAttribute("seatsListDTO") SeatsListDTO selectedSeats, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (selectedSeats.getScreeningId() == null) {
            return "redirect:/movies/movies";
        }

        List<Long> selectedSeatIds = selectedSeats.getSeatIds();
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Selecciona almenys un seient abans de continuar.");
            return "redirect:/screenings/reserve/" + selectedSeats.getScreeningId();
        }

        Screening screening = screeningRepository.findById(selectedSeats.getScreeningId()).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "redirect:/movies/movies";
        }

        SeatsListDTO cart = new SeatsListDTO();
        cart.setScreeningId(selectedSeats.getScreeningId());
        cart.setSeatIds(new ArrayList<>(selectedSeatIds));
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }
}
