package com.example.educontrol.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.educontrol.Entities.ImportacaoExcel;
import com.example.educontrol.repositories.ExcelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExcelService {
    private final ObjectMapper objectMapper;
    private final ExcelRepository repository;

    public ExcelService(ObjectMapper objectMapper, ExcelRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    public ImportacaoExcel importar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo Excel e obrigatorio");
        }
        try (InputStream input = arquivo.getInputStream()) {
            ImportacaoExcel entidade = new ImportacaoExcel();
            entidade.setNomeArquivo(arquivo.getOriginalFilename());
            entidade.setDadosJson(excelParaJson(input));
            return repository.save(entidade);
        } catch (org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Arquivo invalido: envie uma planilha Excel no formato .xlsx", exception);
        }
    }

    public String excelParaJson(InputStream inputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) return "[]";
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> linhas = sheet.iterator();
            if (!linhas.hasNext()) return "[]";

            Row header = linhas.next();
            int lastColumn = header.getLastCellNum();
            if (lastColumn <= 0) return "[]";
            List<String> cabecalhos = new ArrayList<>(lastColumn);
            for (int i = 0; i < lastColumn; i++) {
                String nome = String.valueOf(valorDaCelula(header.getCell(i)));
                if (nome.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Todos os cabecalhos da planilha devem estar preenchidos");
                }
                if (cabecalhos.contains(nome)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Cabecalho duplicado na planilha: " + nome);
                }
                cabecalhos.add(nome);
            }

            List<Map<String, Object>> resultado = new ArrayList<>();
            while (linhas.hasNext()) {
                Row row = linhas.next();
                Map<String, Object> item = new LinkedHashMap<>();
                boolean hasData = false;
                for (int i = 0; i < cabecalhos.size(); i++) {
                    Object value = valorDaCelula(row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                    hasData |= !(value instanceof String text && text.isBlank());
                    item.put(cabecalhos.get(i), value);
                }
                if (hasData) resultado.add(item);
            }
            try {
                return objectMapper.writeValueAsString(resultado);
            } catch (JsonProcessingException exception) {
                throw new IOException("Nao foi possivel converter a planilha para JSON", exception);
            }
        }
    }

    private Object valorDaCelula(Cell cell) {
        if (cell == null) return "";
        CellType tipo = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType() : cell.getCellType();
        return switch (tipo) {
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toString() : cell.getNumericCellValue();
            default -> "";
        };
    }
}
