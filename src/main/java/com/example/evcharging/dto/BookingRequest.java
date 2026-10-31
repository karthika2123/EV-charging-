package com.example.evcharging.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Station id is required")
    private Long stationId;

    @NotNull(message = "Slot time is required")
    private LocalDateTime slotTime;

    // how many kWh the user wants to charge, used to calculate the price
    @NotNull(message = "kWh is required")
    @Positive(message = "kWh must be greater than 0")
    private Double kwh;
}
