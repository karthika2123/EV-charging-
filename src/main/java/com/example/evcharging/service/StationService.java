package com.example.evcharging.service;

import com.example.evcharging.dto.StationDTO;
import com.example.evcharging.entity.ChargingStation;
import com.example.evcharging.entity.Role;
import com.example.evcharging.entity.User;
import com.example.evcharging.exception.BadRequestException;
import com.example.evcharging.exception.ResourceNotFoundException;
import com.example.evcharging.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private AuthService authService;

    public List<StationDTO> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public StationDTO getStationById(Long id) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id " + id));
        return toDTO(station);
    }

    public StationDTO addStation(StationDTO dto) {
        User owner = authService.getCurrentUser();

        ChargingStation station = new ChargingStation();
        station.setStationName(dto.getStationName());
        station.setLocation(dto.getLocation());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setTotalSlots(dto.getTotalSlots());
        station.setAvailableSlots(dto.getTotalSlots()); // all slots are free in the beginning
        station.setPricePerKwh(dto.getPricePerKwh());
        station.setOwner(owner);

        return toDTO(stationRepository.save(station));
    }

    @Transactional
    public StationDTO updateStation(Long id, StationDTO dto) {
        User currentUser = authService.getCurrentUser();

        // findByIdForUpdate locks the row so a booking can't change the slots while we update
        ChargingStation station = stationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id " + id));

        // only the owner or an admin can update
        boolean isOwner = station.getOwner().getId().equals(currentUser.getId());
        if (!isOwner && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only update your own station");
        }

        // slots that are booked right now
        int bookedSlots = station.getTotalSlots() - station.getAvailableSlots();
        if (dto.getTotalSlots() < bookedSlots) {
            throw new BadRequestException("Total slots cannot be less than booked slots (" + bookedSlots + ")");
        }

        station.setStationName(dto.getStationName());
        station.setLocation(dto.getLocation());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setPricePerKwh(dto.getPricePerKwh());
        station.setTotalSlots(dto.getTotalSlots());
        station.setAvailableSlots(dto.getTotalSlots() - bookedSlots);

        return toDTO(stationRepository.save(station));
    }

    // convert entity -> DTO
    private StationDTO toDTO(ChargingStation station) {
        StationDTO dto = new StationDTO();
        dto.setId(station.getId());
        dto.setStationName(station.getStationName());
        dto.setLocation(station.getLocation());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        dto.setTotalSlots(station.getTotalSlots());
        dto.setAvailableSlots(station.getAvailableSlots());
        dto.setPricePerKwh(station.getPricePerKwh());
        dto.setOwnerId(station.getOwner().getId());
        return dto;
    }
}
