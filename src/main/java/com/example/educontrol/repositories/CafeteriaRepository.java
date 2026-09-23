package com.example.educontrol.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.educontrol.cafeteria.Cafeteria;

public interface CafeteriaRepository extends JpaRepository<Cafeteria, UUID> {
}
