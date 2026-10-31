package com.example.evcharging.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private ChargingStation station;

    @Column(name = "slot_time")
    private LocalDateTime slotTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "total_amount")
    private Double totalAmount;
}
