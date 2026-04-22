package com.daw.cinemadaw.controller;

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
    public String mostrarFormulario(@PathVariable Long id, Model model,
                                    RedirectAttributes redirectAttributes) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            model.addAttribute("room", room);
            return "room/edit-room";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "La sala no s'ha trobat.");
        return "redirect:/cinemes";
    }


    @PostMapping("/room/update")
    public String updateRoom(@Valid @ModelAttribute Room room, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "room/edit-room";
        }
        Optional<Room> optional = roomRepository.findById(room.getId());
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La sala no s'ha trobat.");
            return "redirect:/cinemes";
        }
        Room existing = optional.get();
        existing.setName(room.getName());
        existing.setCapacity(room.getCapacity());
        Room savedRoom = roomRepository.save(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Sala actualitzada correctament.");
        if (savedRoom.getCinema() != null) {
            return "redirect:/cinema/" + savedRoom.getCinema().getId();
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/room/{id}/delete")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La sala no s'ha trobat.");
            return "redirect:/cinemes";
        }
        Room room = optional.get();
        Long cinemaId = room.getCinema() != null ? room.getCinema().getId() : null;
        try {
            roomRepository.delete(room);
            redirectAttributes.addFlashAttribute("successMessage", "Sala eliminada correctament.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No s'ha pogut eliminar la sala. Pot tenir seients o projeccions associades.");
        }
        if (cinemaId != null) {
            return "redirect:/cinema/" + cinemaId;
        }
        return "redirect:/cinemes";
    }


    @GetMapping("/room/create")
    public String mostrarFormCreate(Long cinemaId, Model model,
                                    RedirectAttributes redirectAttributes) {
        Optional<Cinema> optional = cinemaRepository.findById(cinemaId);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El cinema no s'ha trobat.");
            return "redirect:/cinemes";
        }
        Room room = new Room();
        room.setCinema(optional.get());
        model.addAttribute("room", room);
        return "room/create-room";
    }

    @PostMapping("/room/create")
    public String createRoom(@Valid @ModelAttribute Room room, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "room/create-room";
        }
        roomRepository.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Sala creada correctament.");
        if (room.getCinema() != null) {
            return "redirect:/cinema/" + room.getCinema().getId();
        }
        return "redirect:/cinemes";
    }

    @GetMapping("/room/{id}")
    public String detailRoom(@PathVariable Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            model.addAttribute("room", room);
            return "room/detail-room";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "La sala no s'ha trobat.");
        return "redirect:/cinemes";
    }
}
