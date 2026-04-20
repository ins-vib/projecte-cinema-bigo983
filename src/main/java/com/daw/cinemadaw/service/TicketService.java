package com.daw.cinemadaw.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.cinemadaw.domain.cinema.Comanda;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.repository.ComandaRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;
import com.daw.cinemadaw.repository.SeatRepository;
import com.daw.cinemadaw.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ComandaRepository comandaRepository;

    public TicketService(TicketRepository ticketRepository, ScreeningRepository screeningRepository,
            SeatRepository seatRepository, ComandaRepository comandaRepository) {
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.comandaRepository = comandaRepository;
    }

    public Map<Long, List<Ticket>> getTicketsGroupedByOrder() {
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

        return ticketsByOrder;
    }

    @Transactional
    public Comanda crearOrdreTickets(Map<Long, List<Long>> cart) {
        // Instruccions: crear l'ordre des del carret (screeningId -> llista de seatIds),
        // crear tickets i persistir-ho tot dins d'una transacció.
        if (cart == null || cart.isEmpty()) {
            return null;
        }

        Comanda comanda = new Comanda();
        comanda.setCreatedAt(LocalDateTime.now());
        comandaRepository.save(comanda);

        for (Entry<Long, List<Long>> cartEntry : cart.entrySet()) {
            Long screeningId = cartEntry.getKey();
            Screening screening = screeningRepository.findById(screeningId).orElse(null);
            if (screening == null) {
                continue;
            }

            for (Long seatId : cartEntry.getValue()) {
                Seat seat = seatRepository.findById(seatId).orElse(null);
                if (seat == null || !seat.isState()) {
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
                    // Si el seient ja s'ha venut per aquesta projecció, es continua amb la resta.
                }
            }
        }

        return comanda;
    }
}
