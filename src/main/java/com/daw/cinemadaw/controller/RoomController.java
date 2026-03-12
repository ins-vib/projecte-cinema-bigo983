package com.daw.cinemadaw.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Cinema;
import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.RoomRepository;

import jakarta.validation.Valid;

@Controller
public class RoomController {
    
    private RoomRepository roomRepository;

    private CinemaRepository cinemaRepository;

    public RoomController(RoomRepository roomRepository, CinemaRepository cinemaRepository) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
    }

@GetMapping("/room/{id}/update")
    public String mostrarFormulario(@PathVariable Long id, Model model) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            model.addAttribute("room", room);
            return "room/edit-room";
        }
        return "redirect:/cinema/{id}";
    }


@PostMapping("/room/update")
    public String updateRoom(@Valid @ModelAttribute Room room, BindingResult result) {
        if (result.hasErrors()) {
            return "room/edit-room";
        }
        Room savedRoom = roomRepository.save(room);
        if (savedRoom.getCinema() != null) {
            return "redirect:/cinema/" + savedRoom.getCinema().getId();
        }
        return "redirect:/cinemes";
    }

@GetMapping("/room/{id}/delete")
    public String deleteRoom(@PathVariable Long id) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            roomRepository.delete(room);
        }
        return "redirect:/cinema/" + optional.get().getCinema().getId();
    }


    @GetMapping("/room/create")
    public String mostrarFormCreate(Long cinemaId, Model model) {
        
        Room room = new Room();
        Optional<Cinema> optional = cinemaRepository.findById(cinemaId);
        room.setCinema(optional.get());
        model.addAttribute("room", room);
        return "room/create-room";
    }
    
    @PostMapping("/room/create")
        public String createRoom(@Valid @ModelAttribute Room room, BindingResult result) {
            if (result.hasErrors()) {
                return "room/create-room";
            }   
            roomRepository.save(room);
            return "redirect:/cinema/" + room.getCinema().getId();
        }

@GetMapping("/room/{id}")
    public String detailRoom(@PathVariable Long id  , Model model) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            model.addAttribute("room", room);
            return "room/detail-room";
        }
        return "redirect:/cinema/{id}";
    }
}
