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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.New;
import com.daw.cinemadaw.domain.cinema.User;
import com.daw.cinemadaw.repository.MovieRepository;
import com.daw.cinemadaw.repository.UserRepository;
import com.daw.cinemadaw.service.NewService;

import jakarta.validation.Valid;

@Controller
public class HomeController {
    
    private final NewService newService;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(NewService newService, UserRepository userRepository, PasswordEncoder passwordEncoder, MovieRepository movieRepository) {
        this.newService = newService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.movieRepository = movieRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        ArrayList<New> newsList = newService.getNews();
        model.addAttribute("newsList", newsList);
        return "home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/home";
    }

    @GetMapping("/admin/news")
    public String adminNews(Model model) {
        model.addAttribute("newsList", newService.getNews());
        return "admin/news";
    }

    @PostMapping("/admin/news")
    public String createNews(
            @RequestParam String headline,
            @RequestParam String body,
            RedirectAttributes redirectAttributes) {

        boolean created = newService.addNews(headline, body);

        if (created) {
            redirectAttributes.addFlashAttribute("newsSuccess", "Noticia publicada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("newsError", "No se ha podido guardar la noticia.");
        }

        return "redirect:/admin/news";
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

        if (userRepository.findByUsername(client.getUsername()).isPresent()) {
            model.addAttribute("registerError", "Aquest usuari ja existeix.");
            return "register";
        }

        client.setRole("CLIENT");
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        userRepository.save(client);
        

        return "redirect:/login?registered";
    }
    
}
