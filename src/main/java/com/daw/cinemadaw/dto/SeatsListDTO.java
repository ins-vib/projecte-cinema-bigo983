package com.daw.cinemadaw.dto;

import java.util.ArrayList;
import java.util.List;

public class SeatsListDTO {
    private Long screeningId;
    private List<Long> seatIds = new ArrayList<>();

    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
