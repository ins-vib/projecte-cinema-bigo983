package com.daw.cinemadaw.domain.cinema;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "La fila és obligatòria")
    @Size(min = 1, max = 5, message = "La fila ha de tenir entre 1 i 5 caràcters")
    @Column
    private String seatRow;

    @NotNull(message = "El número és obligatori")
    @Min(value = 1, message = "El número ha d'estar entre 1 i 999")
    @Max(value = 999, message = "El número ha d'estar entre 1 i 999")
    @Column
    private Integer number;

    @NotNull(message = "La coordenada X és obligatòria")
    @Min(value = 0, message = "La coordenada X ha d'estar entre 0 i 79")
    @Max(value = 79, message = "La coordenada X ha d'estar entre 0 i 79")
    @Column
    private Integer x;

    @NotNull(message = "La coordenada Y és obligatòria")
    @Min(value = 0, message = "La coordenada Y ha d'estar entre 0 i 59")
    @Max(value = 59, message = "La coordenada Y ha d'estar entre 0 i 59")
    @Column
    private Integer y;

    @Column
    private boolean state = true;

    @NotNull(message = "El tipus de seient és obligatori")
    @Column
    @Enumerated(EnumType.STRING)
    private SeatType typeSeat = SeatType.STANDARD;

    @ManyToOne
    private Room room;

    public Seat() {
    }


    public Seat(int number, int x, int y, String seatRow, Room room) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.seatRow = seatRow;
        this.room = room;
    }




    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
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
