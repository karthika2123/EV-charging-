package com.example.evcharging.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StationDTO {

    // id, availableSlots and ownerId are only filled in responses
    private Long id;

    @NotBlank(message = "Station name is required")
    private String stationName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Total slots is required")
    @Min(value = 1, message = "Total slots must be at least 1")
    private Integer totalSlots;

    private Integer availableSlots;

    @NotNull(message = "Price per kWh is required")
    @Positive(message = "Price must be greater than 0")
    private Double pricePerKwh;

    private Long ownerId;
}
