package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.repository.CinemaRepository;

@Controller
public class CinemaController {
        private CinemaRepository cinemaRepository;
        
    public CinemaController(CinemaRepository cinemaRepository){
        this.cinemaRepository = cinemaRepository;
    }

    @GetMapping("/cinemes")
    public String cinemes(Model model){
        
        List<Cinema> cinemas = cinemaRepository.findAll();
        model.addAttribute("llista", cinemas);

        return "cinemes/cinemes";
    }
    // Detail cinema
    @GetMapping("/cinema/{id}")
    public String detail(@PathVariable Long id, Model model){

        Optional<Cinema> optional = cinemaRepository.findById(id);
        
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            cinema.getRooms();
            model.addAttribute("cinema", cinema);
            return "cinemes/detail-cinema";
        }
        return "redirect:/";
    }

    @GetMapping("/cinema/create")
    public String mostrarFormulariAlta(Model model){
        Cinema cinema = new Cinema();
        // cinema.setCity("Tarragona"); // Per defecte, el camp ciutat es omplirà amb "Tarragona"
        model.addAttribute("cinema", cinema);
        return "cinemes/create-cinemes";
        
    }

    @PostMapping("/cinema/create")
    public String altaCinema(@ModelAttribute Cinema cinema){
        cinemaRepository.save(cinema);
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

    @GetMapping("/cinema/edit/{id}")
    public String mostrarFormularEdit(@PathVariable Long id, Model model){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            model.addAttribute("cinema", cinema);
            return "cinemes/edit-cinema";
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/cinema/update")
    public String editCinema(@ModelAttribute Cinema cinema){
        cinemaRepository.save(cinema);
        return "redirect:/cinemes";
    }
}
