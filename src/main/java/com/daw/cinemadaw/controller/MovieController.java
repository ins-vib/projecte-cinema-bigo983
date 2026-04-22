package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.repository.MovieRepository;

import jakarta.validation.Valid;

@Controller
public class MovieController {

    private MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies/movies")
    public String movies(Model model) {
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "movies/movies";
    }

    @GetMapping("/movies/create-movies")
    public String mostrarFormMovies(Model model) {
        Movie movie = new Movie();
        model.addAttribute("movie", movie);
        return "movies/create-movies";
    }

    @PostMapping("/movies/create")
    public String createMovie(@Valid @ModelAttribute Movie movie, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "movies/create-movies";
        }
        movieRepository.save(movie);
        redirectAttributes.addFlashAttribute("successMessage", "Pel·lícula creada correctament.");
        return "redirect:/movies/movies";
    }

    @GetMapping("/movies/update/{id}")
    public String mostrarFormulariEdit(@PathVariable Long id, Model model,
                                       RedirectAttributes redirectAttributes) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("movie", optional.get());
            return "movies/edit-movies";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "La pel·lícula no s'ha trobat.");
        return "redirect:/movies/movies";
    }


    @PostMapping("/movies/update")
    public String updateMovie(@Valid @ModelAttribute Movie movie, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "movies/edit-movies";
        }
        Optional<Movie> optional = movieRepository.findById(movie.getId());
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La pel·lícula no s'ha trobat.");
            return "redirect:/movies/movies";
        }
        Movie existing = optional.get();
        existing.setTitle(movie.getTitle());
        existing.setDuration(movie.getDuration());
        existing.setGenre(movie.getGenre());
        existing.setDescription(movie.getDescription());
        existing.setReleaseDate(movie.getReleaseDate());
        movieRepository.save(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Pel·lícula actualitzada correctament.");
        return "redirect:/movies/movies";
    }

    @GetMapping("/movies/detail/{id}")
    public String detailMovie(@PathVariable Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("movie", optional.get());
            return "movies/detail-movies";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "La pel·lícula no s'ha trobat.");
        return "redirect:/movies/movies";
    }

    @PostMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            try {
                movieRepository.delete(optional.get());
                redirectAttributes.addFlashAttribute("successMessage",
                        "Pel·lícula eliminada correctament.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No s'ha pogut eliminar la pel·lícula. Pot tenir projeccions o reserves associades.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "La pel·lícula no s'ha trobat.");
        }
        return "redirect:/movies/movies";
    }
}
