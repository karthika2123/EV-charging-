package com.example.evcharging.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // stored as BCrypt hash
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone_number")
    private String phoneNumber;

    // one user (owner) has many stations
    @OneToMany(mappedBy = "owner")
    private List<ChargingStation> stations = new ArrayList<>();

    // one user has many bookings
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();
}
