package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.SeatRepository;

@Controller
public class SeatController {
    
    private RoomRepository roomRepository;
    private SeatRepository seatRepository;

    public SeatController(RoomRepository roomRepository, SeatRepository seatRepository){
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
    }

    @GetMapping("/seat/{id}")
    public String viewSeatsByRoomId(Model model, @PathVariable("id") Long id) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            List<Seat> seats = room.getSeats();
            model.addAttribute("seats", seats);
            return "seat/detail-seat";
        }
        return "redirect:/cinemes"; 
    }

    @GetMapping("/seats/{id}")
    public String viewSeatById(Model model, @PathVariable("id") Long id) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("seat", optional.get());
            return "seat/change-seat";
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/seats/update/{id}")
    public String updateSeat(@PathVariable("id") Long id, Seat seat) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat1 = optional.get();
            seat1.setX(seat.getX());
            seat1.setY(seat.getY());
            seatRepository.save(seat1);
            return "redirect:/seat/" + seat1.getRoom().getId();
        }
        return "redirect:/cinemes";
    }

}

