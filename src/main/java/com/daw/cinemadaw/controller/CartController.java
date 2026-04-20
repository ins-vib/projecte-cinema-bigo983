package com.daw.cinemadaw.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.cinemadaw.domain.cinema.Comanda;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.dto.SeatsListDTO;
import com.daw.cinemadaw.repository.ComandaRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;
import com.daw.cinemadaw.repository.SeatRepository;
import com.daw.cinemadaw.repository.TicketRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ComandaRepository comandaRepository;
    private final TicketRepository ticketRepository;

    public CartController(ScreeningRepository screeningRepository, SeatRepository seatRepository,
            ComandaRepository comandaRepository, TicketRepository ticketRepository) {
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.comandaRepository = comandaRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("purchaseMessage", session.getAttribute("purchaseMessage"));
        session.removeAttribute("purchaseMessage");
        model.addAttribute("hasItems", false);
        model.addAttribute("totalPrice", 0.0);

        Object cartObject = session.getAttribute("cart");
        if (!(cartObject instanceof SeatsListDTO cart)) {
            return "cart/cart";
        }

        Screening screening = screeningRepository.findById(cart.getScreeningId()).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "cart/cart";
        }

        int quantity = cart.getSeatIds().size();
        double unitPrice = screening.getPrice();
        double totalPrice = unitPrice * quantity;

        model.addAttribute("screening", screening);
        model.addAttribute("selectedSeatIds", cart.getSeatIds());
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("hasItems", quantity > 0);

        return "cart/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        if (!(cartObject instanceof SeatsListDTO cart)) {
            return "redirect:/cart";
        }

        Screening screening = screeningRepository.findById(cart.getScreeningId()).orElse(null);
        if (screening == null) {
            return "redirect:/cart";
        }

        Comanda comanda = new Comanda();
        comanda.setCreatedAt(LocalDateTime.now());
        comandaRepository.save(comanda);

        List<String> alreadyPurchasedSeats = new ArrayList<>();
        for (Long seatId : cart.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId).orElse(null);
            String seatLabel = seatLabel(seat, seatId);

            if (seat == null || !seat.isState()) {
                alreadyPurchasedSeats.add(seatLabel);
                continue;
            }

            Ticket ticket = new Ticket();
            ticket.setPrice(screening.getPrice());
            ticket.setSeat(seat);
            ticket.setScreening(screening);
            ticket.setComanda(comanda);

            try {
                ticketRepository.saveAndFlush(ticket);
                seat.setState(false);
                seatRepository.save(seat);
            } catch (DataIntegrityViolationException ex) {
                alreadyPurchasedSeats.add(seatLabel);
            }
        }

        if (!alreadyPurchasedSeats.isEmpty()) {
            session.setAttribute("purchaseMessage",
                    "No se pudo completar la compra. Asientos ya comprados: "
                            + String.join(", ", alreadyPurchasedSeats) + ".");
            return "redirect:/cart";
        }

        session.removeAttribute("cart");
        session.setAttribute("purchaseMessage", "Compra realizada correctamente. Orden #" + comanda.getId());

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeSeat(@RequestParam Long seatId, HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        if (!(cartObject instanceof SeatsListDTO cart)) {
            return "redirect:/cart";
        }

        cart.getSeatIds().remove(seatId);

        if (cart.getSeatIds().isEmpty()) {
            session.removeAttribute("cart");
        } else {
            session.setAttribute("cart", cart);
        }

        return "redirect:/cart";
    }

    private String seatLabel(Seat seat, Long seatId) {
        if (seat == null) {
            return "ID " + seatId;
        }

        String row = seat.getSeatRow() == null ? "" : seat.getSeatRow();
        return row + seat.getNumber();
    }

}
