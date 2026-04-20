package com.daw.cinemadaw.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.service.TicketService;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        Map<Long, List<Ticket>> ticketsByOrder = ticketService.getTicketsGroupedByOrder();
        model.addAttribute("ticketsByOrder", ticketsByOrder);
        return "tickets/tickets";
    }
}
