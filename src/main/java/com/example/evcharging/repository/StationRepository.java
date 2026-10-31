package com.example.evcharging.repository;

import com.example.evcharging.entity.ChargingStation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StationRepository extends JpaRepository<ChargingStation, Long> {

    // Reduces slots by 1 ONLY if a slot is free. It is one SQL update so two users
    // booking at the same time cannot both get the last slot. Returns 0 if no slot left.
    @Modifying
    @Query("UPDATE ChargingStation s SET s.availableSlots = s.availableSlots - 1 " +
           "WHERE s.id = :id AND s.availableSlots > 0")
    int decreaseSlot(@Param("id") Long id);

    // Adds 1 slot back (used when a booking is cancelled), never above total slots
    @Modifying
    @Query("UPDATE ChargingStation s SET s.availableSlots = s.availableSlots + 1 " +
           "WHERE s.id = :id AND s.availableSlots < s.totalSlots")
    int increaseSlot(@Param("id") Long id);

    // Locks the row while a station is being updated (so bookings can't change it at the same time)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ChargingStation s WHERE s.id = :id")
    Optional<ChargingStation> findByIdForUpdate(@Param("id") Long id);
}
