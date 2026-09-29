import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/excel")


public class ExcelController {
    private final ExcelService service;

    public EscelController(ExcelService service) {
        this.service = service;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacaoExcel> importar(@RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(service.importar(file));
    }
}
