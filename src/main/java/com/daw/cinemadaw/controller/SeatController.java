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

import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.domain.cinema.SeatType;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.SeatRepository;

import jakarta.validation.Valid;

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
            model.addAttribute("room", room);
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
    public String updateSeat(@PathVariable("id") Long id, @Valid @ModelAttribute("seat") Seat seat,
                             BindingResult result, Model model) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat1 = optional.get();
            if (result.hasErrors()) {
                seat.setId(seat1.getId());
                seat.setRoom(seat1.getRoom());
                model.addAttribute("types", SeatType.values());
                return "seat/change-seat";
            }
            seat.setId(seat1.getId());
            seat.setRoom(seat1.getRoom());
            seatRepository.save(seat);
            return "redirect:/seat/" + seat1.getRoom().getId();
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/seats/delete/{id}")
    public String deleteSeat(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat = optional.get();
            Room room = seat.getRoom();
            Long roomId = room.getId();
            try {
                seatRepository.delete(seat);
                if (room.getCapacity() > 0) {
                    room.setCapacity(room.getCapacity() - 1);
                    roomRepository.save(room);
                }
                redirectAttributes.addFlashAttribute("successMessage", "Seient eliminat correctament.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No s'ha pogut eliminar el seient. Pot tenir reserves associades.");
            }
            return "redirect:/seat/" + roomId;
        }
        redirectAttributes.addFlashAttribute("errorMessage", "El seient no s'ha trobat.");
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
    public String createSeat(@PathVariable("roomId") Long roomId, @Valid @ModelAttribute("seat") Seat seat,
                             BindingResult result, Model model) {
        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isPresent()) {
            if (result.hasErrors()) {
                model.addAttribute("roomId", roomId);
                model.addAttribute("types", SeatType.values());
                return "seat/create-seat";
            }
            Room room = optional.get();
            seat.setRoom(room);
            seatRepository.save(seat);
            room.setCapacity(room.getCapacity() + 1);
            roomRepository.save(room);
            return "redirect:/seat/" + roomId;
        }
        return "redirect:/cinemes";
    }
}
