
@Entity 
@Table(name = "importacao_excel")
public class ImportacaoExcel {

    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nomeArquivo;

    // PostgreSQL não tem um tipo JSON nativo, mas o Spring Data JPA pode mapear para String
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String dadosJson;

    private localDateTime criadoEm = LocalDateTime.now();

    

    
}
