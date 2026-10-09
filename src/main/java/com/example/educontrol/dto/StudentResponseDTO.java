package com.example.educontrol.dto;

import java.util.UUID;

import com.example.educontrol.student.Period;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {
    private UUID id;
    private String name;
    private String registration;
    private String classroom;
    private Period turn;
}
