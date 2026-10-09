package com.example.educontrol.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.dto.ApiResponseDTO;
import com.example.educontrol.dto.CafeteriaResponseDTO;
import com.example.educontrol.services.CafeteriaService;

@RestController
@RequestMapping("/api/cafeterias")
public class CafeteriaController {

    private final CafeteriaService cafeteriaService;

    public CafeteriaController(CafeteriaService cafeteriaService) {
        this.cafeteriaService = cafeteriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDTO<CafeteriaResponseDTO>> createCafeteria(
            @RequestBody Cafeteria cafeteria) {
        CafeteriaResponseDTO cafeteriaDTO = cafeteriaService.createCafeteria(cafeteria);
        ApiResponseDTO<CafeteriaResponseDTO> response = ApiResponseDTO.<CafeteriaResponseDTO>builder()
                .success(true)
                .message("Cafeteria criada com sucesso")
                .data(cafeteriaDTO)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CafeteriaResponseDTO>>> getAllCafeterias() {
        List<CafeteriaResponseDTO> cafeterias = cafeteriaService.getAllCafeterias();
        ApiResponseDTO<List<CafeteriaResponseDTO>> response = ApiResponseDTO.<List<CafeteriaResponseDTO>>builder()
                .success(true)
                .message("Cafeterias recuperadas com sucesso")
                .data(cafeterias)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<CafeteriaResponseDTO>>> getAllActiveCafeterias() {
        List<CafeteriaResponseDTO> cafeterias = cafeteriaService.getAllActiveCafeterias();
        ApiResponseDTO<List<CafeteriaResponseDTO>> response = ApiResponseDTO.<List<CafeteriaResponseDTO>>builder()
                .success(true)
                .message("Cafeterias ativas recuperadas com sucesso")
                .data(cafeterias)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CafeteriaResponseDTO>> getCafeteriaById(@PathVariable UUID id) {
        CafeteriaResponseDTO cafeteria = cafeteriaService.getCafeteriaById(id);
        ApiResponseDTO<CafeteriaResponseDTO> response = ApiResponseDTO.<CafeteriaResponseDTO>builder()
                .success(true)
                .message("Cafeteria recuperada com sucesso")
                .data(cafeteria)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CafeteriaResponseDTO>> updateCafeteria(@PathVariable UUID id,
            @RequestBody Cafeteria cafeteriaDetails) {
        CafeteriaResponseDTO updatedCafeteria = cafeteriaService.updateCafeteria(id, cafeteriaDetails);
        ApiResponseDTO<CafeteriaResponseDTO> response = ApiResponseDTO.<CafeteriaResponseDTO>builder()
                .success(true)
                .message("Cafeteria atualizada com sucesso")
                .data(updatedCafeteria)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCafeteria(@PathVariable UUID id) {
        cafeteriaService.deleteCafeteria(id);
        return ResponseEntity.noContent().build();
    }
}
