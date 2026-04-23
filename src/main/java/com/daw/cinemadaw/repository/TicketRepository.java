package com.daw.cinemadaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.domain.cinema.Ticket;
import com.daw.cinemadaw.domain.cinema.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderByIdDesc();

    List<Ticket> findAllByOrderByComandaIdDescIdDesc();

    List<Ticket> findByStatusOrderByIdDesc(TicketStatus status);

    Optional<Ticket> findBySeatAndScreeningAndStatus(Seat seat, Screening screening, TicketStatus status);
}
