package com.example.educontrol.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.educontrol.dto.ApiResponseDTO;
import com.example.educontrol.dto.EntryRequestDTO;
import com.example.educontrol.dto.EntryResponseDTO;
import com.example.educontrol.services.EntryService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDTO<EntryResponseDTO>> registerEntry(@RequestBody EntryRequestDTO request) {
        EntryResponseDTO entryDTO = entryService.registerEntry(request.getStudentId(), request.getCafeteriaId());
        ApiResponseDTO<EntryResponseDTO> response = ApiResponseDTO.<EntryResponseDTO>builder()
                .success(true)
                .message("Entrada registrada com sucesso")
                .data(entryDTO)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/exit")
    public ResponseEntity<ApiResponseDTO<EntryResponseDTO>> registerExit(@PathVariable UUID id) {
        EntryResponseDTO entryDTO = entryService.registerExit(id);
        ApiResponseDTO<EntryResponseDTO> response = ApiResponseDTO.<EntryResponseDTO>builder()
                .success(true)
                .message("Saída registrada com sucesso")
                .data(entryDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EntryResponseDTO>>> getAllEntries() {
        List<EntryResponseDTO> entries = entryService.getAllEntries();
        ApiResponseDTO<List<EntryResponseDTO>> response = ApiResponseDTO.<List<EntryResponseDTO>>builder()
                .success(true)
                .message("Entradas recuperadas com sucesso")
                .data(entries)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EntryResponseDTO>> getEntryById(@PathVariable UUID id) {
        EntryResponseDTO entry = entryService.getEntryById(id);
        ApiResponseDTO<EntryResponseDTO> response = ApiResponseDTO.<EntryResponseDTO>builder()
                .success(true)
                .message("Entrada recuperada com sucesso")
                .data(entry)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteEntry(@PathVariable UUID id) {
        entryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}
