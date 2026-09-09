package com.example.educontrol.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.educontrol.student.Student;

public interface CafeteriaRepository extends JpaRepository<Student, UUID> {
}