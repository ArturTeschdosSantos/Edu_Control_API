package com.example.educontrol.Controllers;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.repositories.CafeteriaRepository;

@RestController
@RequestMapping("/cafeteria")
public class CafeteriaController {
    private final CafeteriaRepository cafeteriaRepository;

    public CafeteriaController(CafeteriaRepository cafeteriaRepository) {
        this.cafeteriaRepository = cafeteriaRepository;
    }

    @GetMapping
    public List<Cafeteria> getAllCafeterias() {
        return cafeteriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Cafeteria getCafeteria(@PathVariable UUID id) {
        return cafeteriaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Refeitorio nao encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cafeteria createCafeteria(@RequestBody Cafeteria cafeteria) {
        validate(cafeteria);
        cafeteria.setId(null);
        return cafeteriaRepository.saveAndFlush(cafeteria);
    }

    @PutMapping("/{id}")
    public Cafeteria updateCafeteria(@PathVariable UUID id, @RequestBody Cafeteria cafeteria) {
        Cafeteria existing = getCafeteria(id);
        validate(cafeteria);
        existing.setName(cafeteria.getName());
        existing.setLocation(cafeteria.getLocation());
        existing.setOpeningTime(cafeteria.getOpeningTime());
        existing.setClosingTime(cafeteria.getClosingTime());
        existing.setActive(cafeteria.isActive());
        return cafeteriaRepository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCafeteria(@PathVariable UUID id) {
        cafeteriaRepository.delete(getCafeteria(id));
    }

    private void validate(Cafeteria cafeteria) {
        if (cafeteria.getName() != null) {
            cafeteria.setName(cafeteria.getName().trim());
        }
        if (cafeteria.getName() == null || cafeteria.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e obrigatorio");
        }
        validateLength(cafeteria.getName(), "Nome");
        validateLength(cafeteria.getLocation(), "Localizacao");
        validateTime(cafeteria.getOpeningTime());
        validateTime(cafeteria.getClosingTime());
    }

    private void validateLength(String value, String field) {
        if (value != null && value.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    field + " deve ter no maximo 255 caracteres");
        }
    }

    private void validateTime(String time) {
        if (time == null) {
            return;
        }
        try {
            LocalTime.parse(time);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Horario invalido: utilize HH:mm ou HH:mm:ss");
        }
    }
}
