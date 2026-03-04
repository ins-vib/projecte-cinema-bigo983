package com.daw.cinemadaw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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



    
}
