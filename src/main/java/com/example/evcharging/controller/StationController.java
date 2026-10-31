package com.example.evcharging.controller;

import com.example.evcharging.dto.StationDTO;
import com.example.evcharging.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping
    public List<StationDTO> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping("/{id}")
    public StationDTO getStation(@PathVariable Long id) {
        return stationService.getStationById(id);
    }

    @PostMapping
    public ResponseEntity<StationDTO> addStation(@Valid @RequestBody StationDTO dto) {
        return new ResponseEntity<>(stationService.addStation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public StationDTO updateStation(@PathVariable Long id, @Valid @RequestBody StationDTO dto) {
        return stationService.updateStation(id, dto);
    }
}
