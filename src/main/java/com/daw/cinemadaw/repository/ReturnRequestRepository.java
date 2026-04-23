package com.daw.cinemadaw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.ReturnRequest;
import com.daw.cinemadaw.domain.cinema.ReturnStatus;
import com.daw.cinemadaw.domain.cinema.Ticket;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByStatusOrderByRequestedAtDesc(ReturnStatus status);
    Optional<ReturnRequest> findByTicketAndStatus(Ticket ticket, ReturnStatus status);
}
