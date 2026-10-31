package com.example.evcharging.service;

import com.example.evcharging.dto.BookingRequest;
import com.example.evcharging.dto.BookingResponse;
import com.example.evcharging.entity.Booking;
import com.example.evcharging.entity.BookingStatus;
import com.example.evcharging.entity.ChargingStation;
import com.example.evcharging.entity.Role;
import com.example.evcharging.entity.User;
import com.example.evcharging.exception.BadRequestException;
import com.example.evcharging.exception.ResourceNotFoundException;
import com.example.evcharging.exception.SlotNotAvailableException;
import com.example.evcharging.repository.BookingRepository;
import com.example.evcharging.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private AuthService authService;

    // @Transactional: if anything fails, the slot decrease is rolled back too
    @Transactional
    public BookingResponse bookSlot(BookingRequest request) {
        User user = authService.getCurrentUser();

        if (request.getSlotTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Slot time must be in the future");
        }

        ChargingStation station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id " + request.getStationId()));

        // reduce available slots by 1 (returns 0 if there is no free slot)
        int updated = stationRepository.decreaseSlot(station.getId());
        if (updated == 0) {
            throw new SlotNotAvailableException("No slots available at " + station.getStationName());
        }

        // price = price per kWh * kWh
        double total = station.getPricePerKwh() * request.getKwh();
        total = Math.round(total * 100.0) / 100.0;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStation(station);
        booking.setSlotTime(request.getSlotTime());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(total);

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userId) {
        User currentUser = authService.getCurrentUser();

        // a customer can only see their own bookings (admin can see all)
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only view your own bookings");
        }

        return bookingRepository.findByUserIdOrderBySlotTimeDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        User currentUser = authService.getCurrentUser();

        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if (!booking.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only CONFIRMED bookings can be cancelled");
        }

        // give the slot back
        stationRepository.increaseSlot(booking.getStation().getId());

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    // convert entity -> response DTO
    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getUser().getId(),
                b.getStation().getId(),
                b.getStation().getStationName(),
                b.getSlotTime(),
                b.getStatus().name(),
                b.getTotalAmount());
    }
}
