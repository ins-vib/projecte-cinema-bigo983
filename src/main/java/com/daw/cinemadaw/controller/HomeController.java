package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.repository.CinemaRepository;

@Controller
public class HomeController {
    
    private CinemaRepository cinemaRepository;

    public HomeController(CinemaRepository cinemaRepository){
        this.cinemaRepository = cinemaRepository;
    }

    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/cinemes")
    public String cinemes(Model model){
        
        List<Cinema> cinemas = cinemaRepository.findAll();
        model.addAttribute("llista", cinemas);

        return "cinemes";
    }

    // Detail cinema
    @GetMapping("/cinema/{id}")
    public String detail(@PathVariable Long id, Model model){

        Optional<Cinema> optional = cinemaRepository.findById(id);
        
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            cinema.getRooms();
            model.addAttribute("cinema", cinema);
            return "detail-cinema";
        }
        return "redirect:/";
    }

    @GetMapping("/cinema/create")
    public String mostrarFormulariAlta(){
        
        return "create-cinema";
        
    }

    @PostMapping("/cinema/create")
    public String altaCinema(){
        
        return "redirect:/cinemes";
    }

    @GetMapping("/cinema/delete/{id}")
    public String delete(@PathVariable Long id){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            cinemaRepository.delete(cinema);
            // cinemaRepository.deleteById(id);
        }
        return "redirect:/cinemes";
        
    }


}
