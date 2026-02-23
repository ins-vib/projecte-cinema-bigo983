package com.daw.cinemadaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Room;

@Repository
public interface RoomRepository extends  JpaRepository<Room, Long> {



    
}
