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
import com.daw.cinemadaw.domain.cinema.SeatType;
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
            model.addAttribute("roomId", id);
            return "seat/detail-seat";
        }
        return "redirect:/cinemes";
    }
    
    @GetMapping("/seats/{id}")
    public String viewSeatById(Model model, @PathVariable("id") Long id) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("seat", optional.get());
            model.addAttribute("types", SeatType.values());
            return "seat/change-seat";
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/seats/update/{id}")
    public String updateSeat(@PathVariable("id") Long id, Seat seat) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat1 = optional.get();
            seat.setId(seat1.getId());
            seat.setRoom(seat1.getRoom());
            seatRepository.save(seat);
            return "redirect:/seat/" + seat1.getRoom().getId();
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/seats/delete/{id}")
    public String deleteSeat(@PathVariable("id") Long id) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat = optional.get();
            Long roomId = seat.getRoom().getId();
            seatRepository.delete(seat);
            return "redirect:/seat/" + roomId;
        }
        return "redirect:/cinemes";
    }

    @GetMapping("/seats/create/{roomId}")
    public String createSeatForm(Model model, @PathVariable("roomId") Long roomId) {
        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isPresent()) {
            Room room = optional.get();
            Seat seat = new Seat();
            seat.setRoom(room);
            model.addAttribute("seat", seat);
            model.addAttribute("roomId", roomId);
            model.addAttribute("types", SeatType.values());
            return "seat/create-seat";
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/seats/create/{roomId}")
    public String createSeat(@PathVariable("roomId") Long roomId, Seat seat) {
        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isPresent()) {
            seat.setRoom(optional.get());
            seatRepository.save(seat);
            return "redirect:/seat/" + roomId;
        }
        return "redirect:/cinemes";
    }
}

