package com.example.educontrol.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.dto.EntryResponseDTO;
import com.example.educontrol.entry.Entry;
import com.example.educontrol.exceptions.InvalidOperationException;
import com.example.educontrol.exceptions.ResourceNotFoundException;
import com.example.educontrol.repositories.EntryRepository;
import com.example.educontrol.student.Student;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final StudentService studentService;
    private final CafeteriaService cafeteriaService;

    public EntryService(EntryRepository entryRepository, StudentService studentService,
            CafeteriaService cafeteriaService) {
        this.entryRepository = entryRepository;
        this.studentService = studentService;
        this.cafeteriaService = cafeteriaService;
    }

    public EntryResponseDTO registerEntry(UUID studentId, UUID cafeteriaId) {
        Student student = studentService.findStudentById(studentId);
        Cafeteria cafeteria = cafeteriaService.findCafeteriaById(cafeteriaId);

        if (!cafeteria.isActive()) {
            throw new InvalidOperationException("Cafeteria não está ativa");
        }

        Entry entry = new Entry();
        entry.setStudent(student);
        entry.setCafeteria(cafeteria);
        entry.setEntryTime(LocalDateTime.now());
        entry.setCompleted(false);
        entry.setCreatedAt(LocalDateTime.now());

        Entry savedEntry = entryRepository.save(entry);
        return convertToDTO(savedEntry);
    }

    public EntryResponseDTO registerExit(UUID entryId) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada não encontrado"));

        if (entry.isCompleted()) {
            throw new InvalidOperationException("Esta entrada já foi finalizada");
        }

        entry.markAsCompleted();
        Entry updatedEntry = entryRepository.save(entry);
        return convertToDTO(updatedEntry);
    }

    public List<EntryResponseDTO> getAllEntries() {
        return entryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EntryResponseDTO getEntryById(UUID entryId) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada não encontrado"));
        return convertToDTO(entry);
    }

    public void deleteEntry(UUID entryId) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada não encontrado"));
        entryRepository.delete(entry);
    }

    private EntryResponseDTO convertToDTO(Entry entry) {
        long durationMinutes = 0;
        if (entry.isCompleted() && entry.getExitTime() != null) {
            durationMinutes = ChronoUnit.MINUTES.between(entry.getEntryTime(), entry.getExitTime());
        }

        return EntryResponseDTO.builder()
                .id(entry.getId())
                .student(studentService.convertToDTO(entry.getStudent()))
                .cafeteria(cafeteriaService.convertToDTO(entry.getCafeteria()))
                .entryTime(entry.getEntryTime())
                .exitTime(entry.getExitTime())
                .completed(entry.isCompleted())
                .durationMinutes(durationMinutes)
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
