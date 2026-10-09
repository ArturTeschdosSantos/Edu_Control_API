package com.example.educontrol.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.educontrol.student.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByRegistration(String registration);
}