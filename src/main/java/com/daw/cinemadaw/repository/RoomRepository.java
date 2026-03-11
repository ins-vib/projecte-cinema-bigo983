package com.daw.cinemadaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Room;

@Repository
public interface RoomRepository extends  JpaRepository<Room, Long> {


    List<Room> findByName(String name);
    
}
