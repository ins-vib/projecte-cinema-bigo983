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

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.dto.ServicesListDTO;
import com.daw.cinemadaw.repository.CinemaRepository;

import jakarta.validation.Valid;

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
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){

        Optional<Cinema> optional = cinemaRepository.findById(id);

        if(optional.isPresent()){
            Cinema cinema = optional.get();
            cinema.getRooms();
            model.addAttribute("cinema", cinema);
            return "cinemes/detail-cinema";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "El cinema no s'ha trobat.");
        return "redirect:/cinemes";
    }

    @GetMapping("/cinema/create")
    public String mostrarFormulariAlta(Model model){
        Cinema cinema = new Cinema();
        model.addAttribute("cinema", cinema);
        return "cinemes/create-cinemes";

    }

    @PostMapping("/cinema/create")
    public String altaCinema(@Valid @ModelAttribute Cinema cinema, BindingResult result,
                             RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            return "cinemes/create-cinemes";
        }
        cinemaRepository.save(cinema);
        redirectAttributes.addFlashAttribute("successMessage", "Cinema creat correctament.");
        return "redirect:/cinemes";
    }

    @PostMapping("/cinema/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            try {
                cinemaRepository.delete(optional.get());
                redirectAttributes.addFlashAttribute("successMessage", "Cinema eliminat correctament.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No s'ha pogut eliminar el cinema. Pot tenir sales o projeccions associades.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El cinema no s'ha trobat.");
        }
        return "redirect:/cinemes";
    }

    @GetMapping("/cinema/edit/{id}")
    public String mostrarFormularEdit(@PathVariable Long id, Model model,
                                      RedirectAttributes redirectAttributes){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            model.addAttribute("cinema", cinema);
            return "cinemes/edit-cinema";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "El cinema no s'ha trobat.");
        return "redirect:/cinemes";
    }

    @PostMapping("/cinema/update")
    public String editCinema(@Valid @ModelAttribute Cinema cinema, BindingResult result,
                             RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "cinemes/edit-cinema";
        }
        Optional<Cinema> optional = cinemaRepository.findById(cinema.getId());
        if(optional.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "El cinema no s'ha trobat.");
            return "redirect:/cinemes";
        }
        Cinema existing = optional.get();
        existing.setCinemaName(cinema.getCinemaName());
        existing.setAddress(cinema.getAddress());
        existing.setCity(cinema.getCity());
        existing.setPostalCode(cinema.getPostalCode());
        cinemaRepository.save(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Cinema actualitzat correctament.");
        return "redirect:/cinemes";
    }

    @GetMapping("/services")
    public String showForm(Model model) {

        model.addAttribute("servicesDTO", new ServicesListDTO());

        model.addAttribute("allServices", List.of(
            "crispetes",
            "parking",
            "begudes",
            "vip",
            "imax"
        ));

        return "cinemes/services-form";
    }

    @PostMapping("/services")
    public String save(@ModelAttribute ServicesListDTO dto) {
        System.out.println(dto.getServices());
        return "redirect:/";
    }
}
