package com.example.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long stationId;
    private String stationName;
    private LocalDateTime slotTime;
    private String status;
    private Double totalAmount;
}
