package com.example.educontrol.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryResponseDTO {
    private UUID id;
    private StudentResponseDTO student;
    private CafeteriaResponseDTO cafeteria;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean completed;
    private long durationMinutes;
    private LocalDateTime createdAt;
}
