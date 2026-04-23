package com.daw.cinemadaw.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.cinemadaw.domain.cinema.ReturnRequest;
import com.daw.cinemadaw.domain.cinema.ReturnStatus;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.domain.cinema.TicketStatus;
import com.daw.cinemadaw.repository.ReturnRequestRepository;
import com.daw.cinemadaw.repository.SeatRepository;
import com.daw.cinemadaw.repository.TicketRepository;
import com.daw.cinemadaw.service.TicketService;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public TicketController(TicketService ticketService, TicketRepository ticketRepository,
                            SeatRepository seatRepository, ReturnRequestRepository returnRequestRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        Map<Long, List<Ticket>> ticketsByOrder = ticketService.getTicketsGroupedByOrder();
        model.addAttribute("ticketsByOrder", ticketsByOrder);
        return "tickets/tickets";
    }

    @PostMapping("/tickets/{id}/return")
    public String requestReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Ticket> optional = ticketRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Entrada no trobada.");
            return "redirect:/tickets";
        }
        Ticket ticket = optional.get();
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aquesta entrada no es pot retornar en el seu estat actual.");
            return "redirect:/tickets";
        }
        if (returnRequestRepository.findByTicketAndStatus(ticket, ReturnStatus.PENDING).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ja hi ha una sol·licitud pendent per aquesta entrada.");
            return "redirect:/tickets";
        }

        ReturnRequest rr = new ReturnRequest();
        rr.setTicket(ticket);
        rr.setRequestedAt(LocalDateTime.now());
        rr.setStatus(ReturnStatus.PENDING);
        returnRequestRepository.save(rr);

        ticket.setStatus(TicketStatus.RETURN_REQUESTED);
        ticketRepository.save(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Sol·licitud de devolució enviada. Pendent de confirmació per l'administrador.");
        return "redirect:/tickets";
    }

    @GetMapping("/admin/returns")
    public String adminReturns(Model model) {
        List<ReturnRequest> pending = returnRequestRepository.findByStatusOrderByRequestedAtDesc(ReturnStatus.PENDING);
        model.addAttribute("pendingReturns", pending);
        return "admin/returns";
    }

    @PostMapping("/admin/returns/{id}/confirm")
    public String confirmReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ReturnRequest> optional = returnRequestRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sol·licitud no trobada.");
            return "redirect:/admin/returns";
        }
        ReturnRequest rr = optional.get();
        rr.setStatus(ReturnStatus.CONFIRMED);
        rr.setResolvedAt(LocalDateTime.now());

        Ticket ticket = rr.getTicket();
        ticket.setStatus(TicketStatus.CANCELLED);
        Seat seat = ticket.getSeat();
        if (seat != null) {
            seat.setState(true);
            seatRepository.save(seat);
        }
        ticketRepository.save(ticket);
        returnRequestRepository.save(rr);

        redirectAttributes.addFlashAttribute("successMessage", "Devolució confirmada. El seient torna a estar disponible.");
        return "redirect:/admin/returns";
    }

    @PostMapping("/admin/returns/{id}/reject")
    public String rejectReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ReturnRequest> optional = returnRequestRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sol·licitud no trobada.");
            return "redirect:/admin/returns";
        }
        ReturnRequest rr = optional.get();
        rr.setStatus(ReturnStatus.REJECTED);
        rr.setResolvedAt(LocalDateTime.now());

        Ticket ticket = rr.getTicket();
        ticket.setStatus(TicketStatus.ACTIVE);
        ticketRepository.save(ticket);
        returnRequestRepository.save(rr);

        redirectAttributes.addFlashAttribute("successMessage", "Sol·licitud rebutjada. L'entrada segueix activa.");
        return "redirect:/admin/returns";
    }
}
