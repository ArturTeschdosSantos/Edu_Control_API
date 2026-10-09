package com.example.educontrol.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.dto.CafeteriaResponseDTO;
import com.example.educontrol.exceptions.ResourceNotFoundException;
import com.example.educontrol.repositories.CafeteriaRepository;

@Service
public class CafeteriaService {

    private final CafeteriaRepository cafeteriaRepository;

    public CafeteriaService(CafeteriaRepository cafeteriaRepository) {
        this.cafeteriaRepository = cafeteriaRepository;
    }

    public CafeteriaResponseDTO createCafeteria(Cafeteria cafeteria) {
        Cafeteria savedCafeteria = cafeteriaRepository.save(cafeteria);
        return convertToDTO(savedCafeteria);
    }

    public List<CafeteriaResponseDTO> getAllCafeterias() {
        return cafeteriaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CafeteriaResponseDTO> getAllActiveCafeterias() {
        return cafeteriaRepository.findByActive(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CafeteriaResponseDTO getCafeteriaById(UUID id) {
        Cafeteria cafeteria = findCafeteriaById(id);
        return convertToDTO(cafeteria);
    }

    public CafeteriaResponseDTO updateCafeteria(UUID id, Cafeteria cafeteriaDetails) {
        Cafeteria cafeteria = findCafeteriaById(id);
        
        if (cafeteriaDetails.getName() != null) {
            cafeteria.setName(cafeteriaDetails.getName());
        }
        if (cafeteriaDetails.getLocation() != null) {
            cafeteria.setLocation(cafeteriaDetails.getLocation());
        }
        if (cafeteriaDetails.getOpeningTime() != null) {
            cafeteria.setOpeningTime(cafeteriaDetails.getOpeningTime());
        }
        if (cafeteriaDetails.getClosingTime() != null) {
            cafeteria.setClosingTime(cafeteriaDetails.getClosingTime());
        }
        cafeteria.setActive(cafeteriaDetails.isActive());

        Cafeteria updatedCafeteria = cafeteriaRepository.save(cafeteria);
        return convertToDTO(updatedCafeteria);
    }

    public void deleteCafeteria(UUID id) {
        Cafeteria cafeteria = findCafeteriaById(id);
        cafeteriaRepository.delete(cafeteria);
    }

    protected Cafeteria findCafeteriaById(UUID id) {
        return cafeteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cafeteria não encontrada com ID: " + id));
    }

    protected CafeteriaResponseDTO convertToDTO(Cafeteria cafeteria) {
        return CafeteriaResponseDTO.builder()
                .id(cafeteria.getId())
                .name(cafeteria.getName())
                .location(cafeteria.getLocation())
                .openingTime(cafeteria.getOpeningTime())
                .closingTime(cafeteria.getClosingTime())
                .active(cafeteria.isActive())
                .build();
    }
}
