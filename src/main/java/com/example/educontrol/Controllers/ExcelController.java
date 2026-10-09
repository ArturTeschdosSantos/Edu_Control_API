package com.example.educontrol.Controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.educontrol.Entities.ImportacaoExcel;
import com.example.educontrol.service.ExcelService;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService service;

    public ExcelController(ExcelService service) {
        this.service = service;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImportacaoExcel importar(@RequestParam("file") MultipartFile file) throws IOException {
        return service.importar(file);
    }
}
