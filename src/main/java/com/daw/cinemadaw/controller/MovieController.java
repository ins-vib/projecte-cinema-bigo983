package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.repository.MovieRepository;

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
    public String createMovie(@ModelAttribute Movie movie) {
        movieRepository.save(movie);
        return "redirect:/movies/movies";
    }

    @GetMapping("/movies/update/{id}")
    public String mostrarFormulariEdit(@PathVariable Long id, Model model) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            Movie movie = optional.get();
            model.addAttribute("movie", movie);
            return "movies/edit-movies";
        }
        return "redirect:/movies/movies";
    }
    

    @PostMapping("/movies/update")
    public String updateMovie(@ModelAttribute Movie movie) {
        movieRepository.save(movie);
        return "redirect:/movies/movies";
    }

    @GetMapping("/movies/detail/{id}")
    public String detailMovie(@PathVariable Long id, Model model) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            Movie movie = optional.get();
            model.addAttribute("movie", movie);
            return "movies/detail-movies";
        }
        return "redirect:/movies/movies";
    }

    @GetMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isPresent()) {
            Movie movie = optional.get();
            movieRepository.delete(movie);
        }
        return "redirect:/movies/movies";
    }
}
