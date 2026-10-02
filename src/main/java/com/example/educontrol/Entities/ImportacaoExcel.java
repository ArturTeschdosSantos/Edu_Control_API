package com.example.educontrol.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "importacao_excel")
@Getter
@Setter
@NoArgsConstructor
public class ImportacaoExcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String nomeArquivo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String dadosJson;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void definirDataCriacao() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }
}
