package com.example.educontrol.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.educontrol.Entities.ImportacaoExcel;

public interface ExcelRepository extends JpaRepository<ImportacaoExcel, Long> {
}
