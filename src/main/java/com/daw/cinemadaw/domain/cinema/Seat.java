package com.daw.cinemadaw.domain.cinema;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column
    private String seatRow;
    
    @Column
    private int number;
    
    @Column
    private int x;
    
    @Column
    private int y;

    @Column
    private boolean state = true;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SeatType typeSeat = SeatType.STANDARD;

    @ManyToOne
    private Room room;

    public Seat() {
    }


    public Seat(int number, int x, int y, String seatRow) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.seatRow = seatRow;
    }




    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public SeatType getTypeSeat() {
        return typeSeat;
    }

    public void setTypeSeat(SeatType typeSeat) {
        this.typeSeat = typeSeat;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatRow='" + seatRow + '\'' +
                ", number=" + number +
                ", x=" + x +
                ", y=" + y +
                ", state=" + state +
                ", typeSeat=" + typeSeat +
                ", room=" + room +
                '}';
    }


    
}
