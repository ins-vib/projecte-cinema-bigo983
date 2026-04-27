package com.daw.cinemadaw.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.daw.cinemadaw.domain.cinema.TicketStatus;
import com.daw.cinemadaw.dto.CartScreeningDTO;
import com.daw.cinemadaw.dto.SeatsListDTO;
import com.daw.cinemadaw.repository.ComandaRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;
import com.daw.cinemadaw.repository.SeatRepository;
import com.daw.cinemadaw.repository.TicketRepository;
import com.daw.cinemadaw.service.SeatPricingUtils;

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

        List<SeatsListDTO> cartItems = readCartItems(session);
        if (cartItems.isEmpty()) {
            return "cart/cart";
        }

        List<CartScreeningDTO> cartEntries = new ArrayList<>();
        double totalPrice = 0.0;
        int quantity = 0;

        for (SeatsListDTO cart : cartItems) {
            Screening screening = screeningRepository.findById(cart.getScreeningId()).orElse(null);
            if (screening == null || screening.getRoom() == null) {
                continue;
            }

            CartScreeningDTO entry = new CartScreeningDTO();
            entry.setScreening(screening);

            Map<Long, Double> seatPrices = new LinkedHashMap<>();
            List<Seat> selectedSeats = new ArrayList<>();
            double screeningTotal = 0.0;

            for (Seat seat : screening.getRoom().getSeats()) {
                if (cart.getSeatIds().contains(seat.getId())) {
                    double seatPrice = SeatPricingUtils.calculateTicketPrice(screening.getPrice(), seat.getTypeSeat());
                    seatPrices.put(seat.getId(), seatPrice);
                    selectedSeats.add(seat);
                    screeningTotal += seatPrice;
                }
            }

            entry.setSeats(selectedSeats);
            entry.setSeatPrices(seatPrices);
            entry.setTotalPrice(screeningTotal);
            cartEntries.add(entry);
            quantity += selectedSeats.size();
            totalPrice += screeningTotal;
        }

        if (cartEntries.isEmpty()) {
            return "cart/cart";
        }

        model.addAttribute("cartEntries", cartEntries);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("hasItems", quantity > 0);
        model.addAttribute("premiumSurcharge", SeatPricingUtils.PREMIUM_SURCHARGE);
        model.addAttribute("vipSurcharge", SeatPricingUtils.VIP_SURCHARGE);

        return "cart/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        List<SeatsListDTO> cartItems = readCartItems(session);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Comanda comanda = new Comanda();
        comanda.setCreatedAt(LocalDateTime.now());
        comandaRepository.save(comanda);

        List<String> alreadyPurchasedSeats = new ArrayList<>();

        for (SeatsListDTO cart : cartItems) {
            Screening screening = screeningRepository.findById(cart.getScreeningId()).orElse(null);
            if (screening == null) {
                continue;
            }

            for (Long seatId : cart.getSeatIds()) {
                Seat seat = seatRepository.findById(seatId).orElse(null);
                String seatLabel = seatLabel(seat, seatId);

                if (seat == null || !seat.isState()) {
                    alreadyPurchasedSeats.add(screening.getMovie().getTitle() + " - " + seatLabel);
                    continue;
                }

                Ticket ticket = ticketRepository
                        .findBySeatAndScreeningAndStatus(seat, screening, TicketStatus.CANCELLED)
                        .orElse(new Ticket());

                ticket.setPrice(SeatPricingUtils.calculateTicketPrice(screening.getPrice(), seat.getTypeSeat()));
                ticket.setSeat(seat);
                ticket.setScreening(screening);
                ticket.setComanda(comanda);
                ticket.setStatus(TicketStatus.ACTIVE);

                try {
                    ticketRepository.saveAndFlush(ticket);
                    seat.setState(false);
                    seatRepository.save(seat);
                } catch (DataIntegrityViolationException ex) {
                    alreadyPurchasedSeats.add(screening.getMovie().getTitle() + " - " + seatLabel);
                }
            }
        }

        if (!alreadyPurchasedSeats.isEmpty()) {
            session.setAttribute("purchaseMessage",
                    "No es va poder completar la compra. Seients ja comprats: "
                            + String.join(", ", alreadyPurchasedSeats) + ".");
            return "redirect:/cart";
        }

        session.removeAttribute("cart");
        session.setAttribute("purchaseMessage", "Compra realizada correctamente. Orden #" + comanda.getId());

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeSeat(@RequestParam Long seatId, HttpSession session) {
        List<SeatsListDTO> cartItems = readCartItems(session);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        cartItems.removeIf(cart -> {
            cart.getSeatIds().remove(seatId);
            return cart.getSeatIds().isEmpty();
        });

        if (cartItems.isEmpty()) {
            session.removeAttribute("cart");
        } else {
            session.setAttribute("cart", cartItems);
        }

        return "redirect:/cart";
    }

    private List<SeatsListDTO> readCartItems(HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        if (cartObject instanceof SeatsListDTO cart) {
            List<SeatsListDTO> cartItems = new ArrayList<>();
            cartItems.add(cart);
            return cartItems;
        }
        if (cartObject instanceof List<?> rawItems) {
            List<SeatsListDTO> cartItems = new ArrayList<>();
            for (Object item : rawItems) {
                if (item instanceof SeatsListDTO cart) {
                    cartItems.add(cart);
                }
            }
            return cartItems;
        }
        return new ArrayList<>();
    }

    private String seatLabel(Seat seat, Long seatId) {
        if (seat == null) {
            return "ID " + seatId;
        }

        String row = seat.getSeatRow() == null ? "" : seat.getSeatRow();
        return row + seat.getNumber();
    }

}
