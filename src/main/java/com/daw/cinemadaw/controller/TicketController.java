package com.daw.cinemadaw.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.repository.TicketRepository;

@Controller
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAllByOrderByComandaIdDescIdDesc();
        Map<Long, List<Ticket>> ticketsByOrder = new LinkedHashMap<>();

        for (Ticket ticket : tickets) {
            if (ticket.getComanda() == null) {
                continue;
            }

            Long orderId = ticket.getComanda().getId();
            ticketsByOrder
                    .computeIfAbsent(orderId, key -> new ArrayList<>())
                    .add(ticket);
        }

        model.addAttribute("ticketsByOrder", ticketsByOrder);
        return "tickets/tickets";
    }
}
