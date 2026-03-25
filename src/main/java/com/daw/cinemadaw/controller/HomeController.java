package com.daw.cinemadaw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.New;
import com.daw.cinemadaw.domain.cinema.User;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.MovieRepository;
import com.daw.cinemadaw.repository.UserRepository;
import com.daw.cinemadaw.service.NewService;

import jakarta.validation.Valid;

@Controller
public class HomeController {
    
    private CinemaRepository cinemaRepository;
    private NewService newService;
    private UserRepository UserRepository;
    private MovieRepository movieRepository;
    private PasswordEncoder passwordEncoder;

    public HomeController(CinemaRepository cinemaRepository, NewService newService, UserRepository userRepository, PasswordEncoder passwordEncoder, MovieRepository movieRepository) {
        this.cinemaRepository = cinemaRepository;
        this.newService = newService;
        this.UserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.movieRepository = movieRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {

        ArrayList<New> newsList = new ArrayList<>();

        try {
            newsList = newService.getNews();
        } catch (Exception e) {
            System.out.println("Error al llegir les notícies: " + e.getMessage());
        }


        model.addAttribute("newsList", newsList);
        return "home";
    }
    @GetMapping("/admin")
    public String admin() {
        return "admin/home";
    }

    // Pàgina de client
    @GetMapping("/client")
    public String client(Model model) {
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "client/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String mostrarFormRegister(Model model) {
     User client = new User();
        model.addAttribute("client", client);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User client, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        client.setRole("CLIENT");
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        UserRepository.save(client);
        

        return "redirect:/login";
    }
    
}
