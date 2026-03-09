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

    public HomeController(CinemaRepository cinemaRepository){
        this.cinemaRepository = cinemaRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {

        NewService news = new NewService();
        ArrayList<New> newsList = new ArrayList<>();
        try {
            newsList = news.getNews();
        } catch (Exception e) {
            System.out.println("Error al llegir les notícies: " + e.getMessage());
        }


        model.addAttribute("newsList", newsList);
        return "home";
    }



    
}
