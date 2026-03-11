package com.daw.cinemadaw.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.daw.cinemadaw.domain.cinema.New;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.service.NewService;

@Controller
public class HomeController {
    
    private CinemaRepository cinemaRepository;
    private NewService newService;

    public HomeController(CinemaRepository cinemaRepository, NewService newService){
        this.cinemaRepository = cinemaRepository;
        this.newService = newService;
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



    
}
