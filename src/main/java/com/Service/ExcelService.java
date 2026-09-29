import com.Service.ExcelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;



@Service
public class ExcelService {

    private final ObjectMapper objectMapper;
    private final ImportacaoExcelRepository repository;

    public ExcelService(ObjectMapper objectMapper, ImportacaoExcelRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    public ImportacaoExcel importar(MultipartFile arquivo) throws IOException {
        String json = excelParaJson(arquivo.getInputStream());

        ImportacaoExcel entidade = new ImportacaoExcel();
        entidade.setNomeArquivo(arquivo.getOriginalFilename());
        entidade.setDadosJson(json);
        return repository.save(entidade);
    }

    public String excelParaJson(InputStream inputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // primeira aba
            Iterator<Row> linhas = sheet.iterator();

            if (!linhas.hasNext()) {
                return "[]";
            }

            // Primeira linha = cabeçalhos
            List<String> cabecalhos = new ArrayList<>();
            for (Cell cell : linhas.next()) {
                cabecalhos.add(valorDaCelula(cell).toString());
            }

            // Demais linhas = dados
            List<Map<String, Object>> resultado = new ArrayList<>();
            while (linhas.hasNext()) {
                Row row = linhas.next();
                Map<String, Object> item = new LinkedHashMap<>();

                for (int i = 0; i < cabecalhos.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    item.put(cabecalhos.get(i), valorDaCelula(cell));
                }
                resultado.add(item);
            }

            return objectMapper.writeValueAsString(resultado);
        }
    }

    private Object valorDaCelula(Cell cell) {
        if (cell == null) return "";

        CellType tipo = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType()
                : cell.getCellType();

        return switch (tipo) {
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toString()
                    : cell.getNumericCellValue();
            default -> "";
        };
    }
}