package com.daw.cinemadaw.service;

import com.daw.cinemadaw.domain.cinema.SeatType;

public final class SeatPricingUtils {

    public static final double PREMIUM_SURCHARGE = 1.50;
    public static final double VIP_SURCHARGE = 3.00;

    private SeatPricingUtils() {
    }

    public static double surchargeFor(SeatType seatType) {
        if (seatType == SeatType.VIP) {
            return VIP_SURCHARGE;
        }
        if (seatType == SeatType.PREMIUM) {
            return PREMIUM_SURCHARGE;
        }
        return 0.0;
    }

    public static double calculateTicketPrice(Double basePrice, SeatType seatType) {
        double base = basePrice == null ? 0.0 : basePrice;
        return roundCurrency(base + surchargeFor(seatType));
    }

    private static double roundCurrency(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
