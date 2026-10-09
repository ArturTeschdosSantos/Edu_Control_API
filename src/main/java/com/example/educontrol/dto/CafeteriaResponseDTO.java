package com.example.educontrol.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeteriaResponseDTO {
    private UUID id;
    private String name;
    private String location;
    private String openingTime;
    private String closingTime;
    private boolean active;
}
