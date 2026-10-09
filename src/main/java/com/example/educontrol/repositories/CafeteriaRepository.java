package com.example.educontrol.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.educontrol.cafeteria.Cafeteria;

@Repository
public interface CafeteriaRepository extends JpaRepository<Cafeteria, UUID> {
    List<Cafeteria> findByActive(boolean active);
}