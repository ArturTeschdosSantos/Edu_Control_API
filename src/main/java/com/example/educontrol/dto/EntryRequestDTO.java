package com.example.educontrol.dto;

import java.util.UUID;

public class EntryRequestDTO {
    private UUID studentId;
    private UUID cafeteriaId;

    public EntryRequestDTO() {
    }

    public EntryRequestDTO(UUID studentId, UUID cafeteriaId) {
        this.studentId = studentId;
        this.cafeteriaId = cafeteriaId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getCafeteriaId() {
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }
}
