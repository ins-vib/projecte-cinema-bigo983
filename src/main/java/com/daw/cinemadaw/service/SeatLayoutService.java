package com.daw.cinemadaw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Seat;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.SeatRepository;

import jakarta.transaction.Transactional;

@Service
public class SeatLayoutService {

    private static final int SEATS_PER_ROW = 10;
    private static final String[] ROW_LETTERS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;

    public SeatLayoutService(SeatRepository seatRepository, RoomRepository roomRepository) {
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void regenerateSeats(Room detachedRoom) {
        Optional<Room> managedOpt = roomRepository.findById(detachedRoom.getId());
        if (managedOpt.isEmpty()) {
            return;
        }
        Room room = managedOpt.get();

        List<Seat> existing = seatRepository.findByRoomId(room.getId());
        if (!existing.isEmpty()) {
            seatRepository.deleteAll(existing);
            seatRepository.flush();
        }

        int capacity = room.getCapacity();
        for (int j = 0; j < capacity; j++) {
            int columna = j % SEATS_PER_ROW;
            int fila = j / SEATS_PER_ROW;
            String rowLetter = rowLabel(fila);
            int x = columna * 5;
            int y = fila * 5;
            Seat seat = new Seat(columna + 1, x, y, rowLetter, room);
            seatRepository.save(seat);
        }
    }

    private String rowLabel(int row) {
        if (row < ROW_LETTERS.length) {
            return ROW_LETTERS[row];
        }
        int extra = row - ROW_LETTERS.length;
        return ROW_LETTERS[extra / ROW_LETTERS.length] + ROW_LETTERS[extra % ROW_LETTERS.length];
    }
}
