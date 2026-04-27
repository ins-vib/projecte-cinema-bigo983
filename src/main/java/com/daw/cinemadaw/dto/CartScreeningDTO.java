package com.daw.cinemadaw.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.domain.cinema.Seat;

public class CartScreeningDTO {
    private Screening screening;
    private List<Seat> seats = new ArrayList<>();
    private Map<Long, Double> seatPrices = new LinkedHashMap<>();
    private double totalPrice;

    public Screening getScreening() {
        return screening;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public Map<Long, Double> getSeatPrices() {
        return seatPrices;
    }

    public void setSeatPrices(Map<Long, Double> seatPrices) {
        this.seatPrices = seatPrices;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return seats == null ? 0 : seats.size();
    }
}