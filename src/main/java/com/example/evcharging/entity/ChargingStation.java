package com.example.evcharging.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_name")
    private String stationName;

    private String location;

    private Double latitude;

    private Double longitude;

    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "available_slots")
    private Integer availableSlots;

    @Column(name = "price_per_kwh")
    private Double pricePerKwh;

    // many stations belong to one owner
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // one station has many bookings
    @OneToMany(mappedBy = "station")
    private List<Booking> bookings = new ArrayList<>();
}
