package com.daw.cinemadaw.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Screening;
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
}
