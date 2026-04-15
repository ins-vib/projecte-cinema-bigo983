package com.daw.cinemadaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderByIdDesc();

    List<Ticket> findAllByOrderByComandaIdDescIdDesc();
}
